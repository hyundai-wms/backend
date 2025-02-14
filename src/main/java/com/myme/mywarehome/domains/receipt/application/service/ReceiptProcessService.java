package com.myme.mywarehome.domains.receipt.application.service;

import com.myme.mywarehome.domains.receipt.application.domain.Receipt;
import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.domain.Return;
import com.myme.mywarehome.domains.receipt.application.domain.service.OutboundProductDomainService;
import com.myme.mywarehome.domains.receipt.application.exception.ReceiptBulkProcessException;
import com.myme.mywarehome.domains.receipt.application.port.in.ReceiptProcessUseCase;
import com.myme.mywarehome.domains.receipt.application.port.in.command.ReceiptOrReturnProcessCommand;
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

import java.util.ArrayList;
import java.util.List;
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
    public Stock process(String outboundProductId, ReceiptOrReturnProcessCommand command) {
        // 1. OutboundProduct 검증 및 연관 된 ReceiptPlan 가져오기
        ReceiptPlan receiptPlan = outboundProductDomainService.validateAndCreateOutboundProduct(outboundProductId);

        // 2. 입고 기록을 생성
        Receipt receipt = Receipt.builder()
                .receiptPlan(receiptPlan)
                .receiptDate(command.selectedDate())
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
        // 1. 입고 예정 정보 가져오기
        List<ReceiptPlan> receiptPlanList = getReceiptPlanPort.findAllReceiptPlansByDate(command.selectedDate());

        if (receiptPlanList.isEmpty()) {
            return;
        }

        List<Receipt> allReceipts = new ArrayList<>();
        List<Return> allReturns = new ArrayList<>();

        // 2. 각 입고 예정건별 처리
        for (ReceiptPlan receiptPlan : receiptPlanList) {
            // 이미 처리된 수량 확인
            long currentProcessedCount = getOutboundProductPort.countByReceiptPlanId(receiptPlan.getReceiptPlanId());
            if (currentProcessedCount >= receiptPlan.getReceiptPlanItemCount()) {
                continue;  // 이미 처리된 건은 스킵
            }

            String productNumber = receiptPlan.getProduct().getProductNumber();
            int totalCount = receiptPlan.getReceiptPlanItemCount();

            // 해당 상품의 반품률 확인 (없으면 0으로 처리)
            double returnRate = command.productReturnRate().getOrDefault(productNumber, 0.0);

            // 입고/반품 수량 계산
            int returnCount = (int) (totalCount * (returnRate / 100));
            int receiptCount = totalCount - returnCount;

            // OutboundProduct 생성 및 각각의 Receipt/Return 생성
            for (int i = 1; i <= totalCount; i++) {
                String outboundProductId = receiptPlan.getReceiptPlanId() + "-" + i;

                try {
                    outboundProductDomainService.createOutboundProductBulk(outboundProductId, receiptPlan);

                    // i가 receiptCount 이하면 입고로, 초과하면 반품으로 처리
                    if (i <= receiptCount) {
                        Receipt receipt = Receipt.builder()
                                .receiptPlan(receiptPlan)
                                .receiptDate(command.selectedDate())
                                .build();
                        allReceipts.add(receipt);
                    } else {
                        Return returnEntity = Return.builder()
                                .receiptPlan(receiptPlan)
                                .returnDate(command.selectedDate())
                                .build();
                        allReturns.add(returnEntity);
                    }
                } catch (Exception e) {
                    throw new ReceiptBulkProcessException();
                }
            }
        }

        // 3. 입고 및 반품 데이터 저장
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
