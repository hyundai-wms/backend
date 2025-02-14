package com.myme.mywarehome.domains.receipt.application.service;

import com.myme.mywarehome.domains.receipt.application.domain.Receipt;
import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.domain.service.OutboundProductDomainService;
import com.myme.mywarehome.domains.receipt.application.port.in.ReceiptProcessUseCase;
import com.myme.mywarehome.domains.receipt.application.port.in.command.ReceiptOrReturnProcessCommand;
import com.myme.mywarehome.domains.receipt.application.port.in.command.ReceiptProcessBulkCommand;
import com.myme.mywarehome.domains.receipt.application.port.in.event.ReceiptCreatedEvent;
import com.myme.mywarehome.domains.receipt.application.port.out.CreateReceiptPort;
import com.myme.mywarehome.domains.stock.application.domain.Stock;
import com.myme.mywarehome.domains.stock.application.exception.StockCreationTimeoutException;
import jakarta.transaction.Transactional;
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
    public void processBulk(ReceiptProcessBulkCommand command) {
        // 1. 입고 예정 정보 가져오기


    }
}
