package com.myme.mywarehome.domains.receipt.application.service;

import com.myme.mywarehome.domains.receipt.application.domain.Receipt;
import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.domain.Return;
import com.myme.mywarehome.domains.receipt.application.domain.service.OutboundProductDomainService;
import com.myme.mywarehome.domains.receipt.application.exception.ReceiptBulkProcessException;
import com.myme.mywarehome.domains.receipt.application.port.in.ReceiptProcessUseCase;
import com.myme.mywarehome.domains.receipt.application.port.in.command.ReceiptProcessBulkCommand;
import com.myme.mywarehome.domains.receipt.application.port.in.event.ReceiptBulkCreatedEvent;
import com.myme.mywarehome.domains.receipt.application.port.in.event.ReceiptCreatedEvent;
import com.myme.mywarehome.domains.receipt.application.port.out.CreateReceiptPort;
import com.myme.mywarehome.domains.receipt.application.port.out.CreateReturnPort;
import com.myme.mywarehome.domains.receipt.application.port.out.GetOutboundProductPort;
import com.myme.mywarehome.domains.receipt.application.port.out.GetReceiptPlanPort;
import com.myme.mywarehome.domains.stock.application.domain.Stock;
import com.myme.mywarehome.domains.stock.application.exception.StockCreationTimeoutException;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReceiptProcessService implements ReceiptProcessUseCase {
    private final OutboundProductDomainService outboundProductDomainService;
    private final CreateReceiptPort createReceiptPort;
    private final ApplicationEventPublisher eventPublisher;
    private final GetReceiptPlanPort getReceiptPlanPort;
    private final CreateReturnPort createReturnPort;
    private final GetOutboundProductPort getOutboundProductPort;

    @Override
    @Transactional
    public Stock process(String outboundProductId, LocalDate selectedDate) {
        // 1. OutboundProduct 검증 및 연관 된 ReceiptPlan 가져오기
        ReceiptPlan receiptPlan = outboundProductDomainService.validateAndCreateOutboundProduct(outboundProductId);

        // 2. 입고 기록을 생성
        Receipt receipt = Receipt.builder()
                .receiptPlan(receiptPlan)
                .receiptDate(selectedDate)
                .build();

        Receipt createdReceipt = createReceiptPort.create(receipt);

        // 3. 재고를 생성 (ApplicationEventPublisher)
        CompletableFuture<Stock> future = new CompletableFuture<>();
        eventPublisher.publishEvent(new ReceiptCreatedEvent(createdReceipt, future));

        // 4. 생성된 재고를 반환
        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new StockCreationTimeoutException();
        }
    }

    @Override
    @Transactional
    public void processBulk(ReceiptProcessBulkCommand command) {
        List<ReceiptPlan> receiptPlanList = getReceiptPlanPort.findAllReceiptPlansByDate(command.selectedDate());
        if (receiptPlanList.isEmpty()) return;

        List<Receipt> allReceipts = new ArrayList<>();
        List<Return> allReturns = new ArrayList<>();

        for (ReceiptPlan receiptPlan : receiptPlanList) {
            // 1. 현재 처리된 상태 확인
            Map<String, Long> processedStatus = getOutboundProductPort.getProcessedStatusByReceiptPlanId(
                    receiptPlan.getReceiptPlanId()
            ); // receiptCount, returnCount 반환
            long currentProcessedCount = processedStatus.get("receiptCount") + processedStatus.get("returnCount");

            if (currentProcessedCount >= receiptPlan.getReceiptPlanItemCount()) {
                continue;
            }

            // 2. 목표 수치 계산
            String productNumber = receiptPlan.getProduct().getProductNumber();
            int totalCount = receiptPlan.getReceiptPlanItemCount();
            double returnRate = command.productReturnRate().getOrDefault(productNumber, 0.0);

            int targetReturnCount = (int) (totalCount * (returnRate / 100));
            int targetReceiptCount = totalCount - targetReturnCount;

            // 3. 현재 처리된 수량과 목표 수량을 비교하여 추가 처리할 수량 결정
            long currentReceiptCount = processedStatus.get("receiptCount");
            long currentReturnCount = processedStatus.get("returnCount");

            // 남은 수량
            int remainingCount = totalCount - (int)currentProcessedCount;

            // 목표 대비 부족한 수량
            int remainingReceiptNeeded = targetReceiptCount - (int)currentReceiptCount;
            int remainingReturnNeeded = targetReturnCount - (int)currentReturnCount;

            // 4. 남은 수량 내에서 최적의 분배 계산
            int additionalReceipts = Math.min(remainingReceiptNeeded, remainingCount);
            int additionalReturns = Math.min(
                    remainingReturnNeeded,
                    remainingCount - additionalReceipts
            );

            // 5. OutboundProduct 생성 및 처리
            int processedThisRound = 0;
            for (int i = (int)currentProcessedCount + 1; i <= totalCount; i++) {
                String outboundProductId = receiptPlan.getReceiptPlanId() + "-" + i;

                try {
                    outboundProductDomainService.createOutboundProductBulk(outboundProductId, receiptPlan);

                    if (processedThisRound < additionalReceipts) {
                        Receipt receipt = Receipt.builder()
                                .receiptPlan(receiptPlan)
                                .receiptDate(command.selectedDate())
                                .build();
                        allReceipts.add(receipt);
                        processedThisRound++;
                    } else if (processedThisRound < (additionalReceipts + additionalReturns)) {
                        Return returnEntity = Return.builder()
                                .receiptPlan(receiptPlan)
                                .returnDate(command.selectedDate())
                                .build();
                        allReturns.add(returnEntity);
                        processedThisRound++;
                    } else {
                        break;  // 더 이상 처리할 필요 없음
                    }
                } catch (Exception e) {
                    throw new ReceiptBulkProcessException();
                }
            }
        }

        // 6. 최종 저장
        if (!allReturns.isEmpty()) {
            allReturns.forEach(createReturnPort::create);
        }

        if (!allReceipts.isEmpty()) {
            List<Receipt> createdReceipts = allReceipts.stream()
                    .map(createReceiptPort::create)
                    .toList();
            eventPublisher.publishEvent(new ReceiptBulkCreatedEvent(createdReceipts));
        }
    }
}

