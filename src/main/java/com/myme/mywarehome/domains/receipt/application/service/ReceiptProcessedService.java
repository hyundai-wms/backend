package com.myme.mywarehome.domains.receipt.application.service;

import com.myme.mywarehome.domains.receipt.application.domain.OutboundProduct;
import com.myme.mywarehome.domains.receipt.application.domain.Receipt;
import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.exception.ReceiptPlanItemCountCapacityExceededException;
import com.myme.mywarehome.domains.receipt.application.exception.DuplicatedOutboundProductException;
import com.myme.mywarehome.domains.receipt.application.exception.ReceiptPlanNotFoundException;
import com.myme.mywarehome.domains.receipt.application.port.in.ReceiptProcessedUseCase;
import com.myme.mywarehome.domains.receipt.application.port.in.command.ReceiptProcessedCommand;
import com.myme.mywarehome.domains.receipt.application.port.in.event.ReceiptCreatedEvent;
import com.myme.mywarehome.domains.receipt.application.port.out.CreateOutboundProductPort;
import com.myme.mywarehome.domains.receipt.application.port.out.CreateReceiptPort;
import com.myme.mywarehome.domains.receipt.application.port.out.GetOutboundProductPort;
import com.myme.mywarehome.domains.receipt.application.port.out.GetReceiptPlanPort;
import com.myme.mywarehome.domains.stock.application.domain.Stock;
import com.myme.mywarehome.domains.stock.application.exception.StockCreationTimeoutException;
import com.myme.mywarehome.infrastructure.util.helper.StringHelper;
import jakarta.transaction.Transactional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReceiptProcessedService implements ReceiptProcessedUseCase {
    private final GetOutboundProductPort getOutboundProductPort;
    private final GetReceiptPlanPort getReceiptPlanPort;
    private final CreateOutboundProductPort createOutboundProductPort;
    private final CreateReceiptPort createReceiptPort;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Stock process(String outboundProductId, ReceiptProcessedCommand command) {
        // 1. 해당 물품이 이미 등록되어있는지 확인
        if(getOutboundProductPort.existsByOutboundProductId(outboundProductId)) {
            throw new DuplicatedOutboundProductException();
        }

        // 2. 파싱하여 입고예정 id 가져오기
        Long receiptPlanId = StringHelper.parseReceiptPlanId(outboundProductId);

        // 3. 물품이 더 이상 들어갈 수 있는지 확인(입고 예정 수량만큼 체크)
        ReceiptPlan receiptPlan = getReceiptPlanPort.findReceiptPlanById(receiptPlanId)
                .orElseThrow(ReceiptPlanNotFoundException::new);

        long currentProcessedCount = getOutboundProductPort.countByReceiptPlanId(receiptPlanId);

        if(receiptPlan.getReceiptPlanItemCount() <= currentProcessedCount) {
            throw new ReceiptPlanItemCountCapacityExceededException();
        }

        // 4. 가능하다면 OutboundProduct에 기록
        OutboundProduct outboundProduct = OutboundProduct.builder()
                .outboundProductId(outboundProductId)
                .receiptPlanId(receiptPlanId)
                .build();

        createOutboundProductPort.create(outboundProduct);

        // 5. 입고 기록을 생성
        Receipt receipt = Receipt.builder()
                .receiptPlan(receiptPlan)
                .receiptDate(command.selectedDate())
                .build();

        Receipt createdReceipt = createReceiptPort.create(receipt);

        // 6. 재고를 생성 (ApplicationEventPublisher)
        CompletableFuture<Stock> future = new CompletableFuture<>();
        eventPublisher.publishEvent(new ReceiptCreatedEvent(createdReceipt, future));

        // 생성된 재고를 반환
        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new StockCreationTimeoutException();
        }
    }
}
