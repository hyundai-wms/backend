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
import com.myme.mywarehome.domains.receipt.application.port.in.event.ReceiptPlanBulkStatusChangedEvent;
import com.myme.mywarehome.domains.receipt.application.port.in.event.ReceiptPlanStatusChangedEvent;
import com.myme.mywarehome.domains.receipt.application.port.out.CreateReceiptPort;
import com.myme.mywarehome.domains.receipt.application.port.out.CreateReturnPort;
import com.myme.mywarehome.domains.receipt.application.port.out.GetOutboundProductPort;
import com.myme.mywarehome.domains.receipt.application.port.out.GetReceiptPlanPort;
import com.myme.mywarehome.domains.receipt.application.port.out.GetReceiptPort;
import com.myme.mywarehome.domains.receipt.application.port.out.GetReturnPort;
import com.myme.mywarehome.domains.stock.adapter.in.event.event.BayBulkUpdateEvent;
import com.myme.mywarehome.domains.stock.adapter.in.event.event.BayUpdateEvent;
import com.myme.mywarehome.domains.stock.adapter.in.event.event.StockBulkUpdateEvent;
import com.myme.mywarehome.domains.stock.adapter.in.event.event.StockUpdateEvent;
import com.myme.mywarehome.domains.stock.application.domain.Stock;
import com.myme.mywarehome.domains.stock.application.exception.StockCreationTimeoutException;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
    private final GetReceiptPort getReceiptPort;
    private final GetReturnPort getReturnPort;

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

        // 4. ReceiptPlan 상태 변경 이벤트 발행
        eventPublisher.publishEvent(new ReceiptPlanStatusChangedEvent(
                receiptPlan.getReceiptPlanId(),
                selectedDate,
                "RECEIPT_PROCESSED"
        ));

        // 5. Stock 상태 변경 이벤트 발행
        eventPublisher.publishEvent(new StockUpdateEvent(receiptPlan.getProduct().getProductNumber()));

        // 6. Bay 상태 변경 이벤트 발행
        eventPublisher.publishEvent(new BayUpdateEvent(receiptPlan.getProduct().getProductNumber()));

        // 7. 생성된 재고를 반환
        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new StockCreationTimeoutException();
        }
    }

    @Override
    @Transactional
    public void processBulk(ReceiptProcessBulkCommand command, LocalDate selectedDate) {
        List<ReceiptPlan> receiptPlanList = getReceiptPlanPort.findAllReceiptPlansByDate(selectedDate);
        if (receiptPlanList.isEmpty()) return;

        List<Receipt> allReceipts = new ArrayList<>();
        List<Return> allReturns = new ArrayList<>();
        Set<Long> processedPlanIds = new HashSet<>();
        Set<String> processedProductNumbers = new HashSet<>();

        for (ReceiptPlan receiptPlan : receiptPlanList) {
            // 1. 기존 OutboundProduct ID들을 조회
            List<String> existingIds = getOutboundProductPort.findOutboundProductIdsByReceiptPlanId(
                    receiptPlan.getReceiptPlanId()
            );

            // 필요한 총 개수
            int totalRequired = receiptPlan.getReceiptPlanItemCount();

            // 이미 존재하는 ID들의 sequence 번호 추출
            Set<Integer> usedSequences = existingIds.stream()
                    .map(id -> Integer.parseInt(id.substring(id.lastIndexOf("-") + 1)))
                    .collect(Collectors.toSet());

            // 사용 가능한 sequence 번호들 찾기 (1부터 totalRequired까지 중에서)
            List<Integer> availableSequences = IntStream.rangeClosed(1, totalRequired)
                    .filter(i -> !usedSequences.contains(i))
                    .boxed()
                    .toList();

            // 이미 처리된 개수가 필요 개수보다 많으면 스킵
            if (existingIds.size() >= totalRequired) {
                continue;
            }

            Map<String, Long> processedStatus = getProcessedStatusByReceiptPlanId(
                    receiptPlan.getReceiptPlanId()
            );

            // 목표 수치 계산
            String productNumber = receiptPlan.getProduct().getProductNumber();
            double returnRate = command.productReturnRate().getOrDefault(productNumber, 0.0);

            int targetReturnCount = (int) (totalRequired * (returnRate / 100));
            int targetReceiptCount = totalRequired - targetReturnCount;

            long currentReceiptCount = processedStatus.get("receiptCount");
            long currentReturnCount = processedStatus.get("returnCount");

            // 추가로 처리해야 할 개수 계산
            int remainingReceiptNeeded = targetReceiptCount - (int)currentReceiptCount;
            int remainingReturnNeeded = targetReturnCount - (int)currentReturnCount;

            // 사용 가능한 sequence 개수 내에서 처리
            int availableCount = availableSequences.size();
            int additionalReceipts = Math.min(remainingReceiptNeeded, availableCount);
            int additionalReturns = Math.min(
                    remainingReturnNeeded,
                    availableCount - additionalReceipts
            );

            // OutboundProduct 생성 및 처리
            int processedThisRound = 0;
            for (Integer seq : availableSequences) {
                String outboundProductId = receiptPlan.getReceiptPlanId() + "-" + seq;

                try {
                    outboundProductDomainService.createOutboundProductBulk(outboundProductId, receiptPlan);

                    if (processedThisRound < additionalReceipts) {
                        Receipt receipt = Receipt.builder()
                                .receiptPlan(receiptPlan)
                                .receiptDate(selectedDate)
                                .build();
                        allReceipts.add(receipt);
                        processedThisRound++;
                        processedPlanIds.add(receiptPlan.getReceiptPlanId());
                        processedProductNumbers.add(receiptPlan.getProduct().getProductNumber());
                    } else if (processedThisRound < (additionalReceipts + additionalReturns)) {
                        Return returnEntity = Return.builder()
                                .receiptPlan(receiptPlan)
                                .returnDate(selectedDate)
                                .build();
                        allReturns.add(returnEntity);
                        processedThisRound++;
                        processedPlanIds.add(receiptPlan.getReceiptPlanId());
                        processedProductNumbers.add(receiptPlan.getProduct().getProductNumber());
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    throw new ReceiptBulkProcessException();
                }
            }

        }

        // 최종 저장 로직
        if (!allReturns.isEmpty()) {
            allReturns.forEach(createReturnPort::create);
        }

        if (!allReceipts.isEmpty()) {
            List<Receipt> createdReceipts = allReceipts.stream()
                    .map(createReceiptPort::create)
                    .toList();
            eventPublisher.publishEvent(new ReceiptBulkCreatedEvent(createdReceipts));
        }

        // 한 번에 모든 변경된 plan들의 상태 변경 이벤트 발행
        if (!processedPlanIds.isEmpty()) {
            eventPublisher.publishEvent(new ReceiptPlanBulkStatusChangedEvent(
                    new ArrayList<>(processedPlanIds),
                    selectedDate,
                    "BULK_PROCESSED"
            ));
        }

        // 한 번에 모든 변경된 plan들의 상태 변경 이벤트 발행
        if (!processedProductNumbers.isEmpty()) {
            eventPublisher.publishEvent(new StockBulkUpdateEvent(
                    new ArrayList<>(processedProductNumbers)
            ));

            eventPublisher.publishEvent(new BayBulkUpdateEvent(
                    new ArrayList<>(processedProductNumbers)
            ));
        }
    }

    private Map<String, Long> getProcessedStatusByReceiptPlanId(Long receiptPlanId) {
        // 전체 생성된 OutboundProduct 수
        long totalProcessed = getOutboundProductPort.countByReceiptPlanId(receiptPlanId);
        // 실제 입고/반품 처리된 수
        long receiptCount = getReceiptPort.countByReceiptPlanId(receiptPlanId);
        long returnCount = getReturnPort.countByReceiptPlanId(receiptPlanId);

        return Map.of(
                "totalProcessed", totalProcessed,
                "receiptCount", receiptCount,
                "returnCount", returnCount
        );
    }
}

