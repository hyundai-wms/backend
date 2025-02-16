package com.myme.mywarehome.domains.stock.adapter.in.event;

import com.myme.mywarehome.domains.receipt.application.port.in.event.ReceiptBulkCreatedEvent;
import com.myme.mywarehome.domains.receipt.application.port.in.event.ReceiptCreatedEvent;
import com.myme.mywarehome.domains.stock.adapter.in.exception.AsyncStockCreationException;
import com.myme.mywarehome.domains.stock.application.domain.Stock;
import com.myme.mywarehome.domains.stock.application.port.in.CreateStockUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiptEventListener {
    private final CreateStockUseCase createStockUseCase;

    @EventListener
    public void handleReceiptCreatedEvent(ReceiptCreatedEvent event) {
        try {
            Stock createdStock = createStockUseCase.createStock(event.receipt());
            event.result().complete(createdStock);
        } catch (Exception e) {
            event.result().completeExceptionally(e);
        }
    }

    @Async
    @TransactionalEventListener
    public void handleReceiptBulkProcess(ReceiptBulkCreatedEvent event) {
        String eventId = UUID.randomUUID().toString();

        try {
            createStockUseCase.createStockBulk(event.receiptList());

            log.info("Bulk stock creation successful. EventId: {}, ReceiptCount: {}",
                    eventId,
                    event.receiptList().size());
        } catch (Exception e) {
            throw new AsyncStockCreationException(eventId, event.receiptList(), e);
        }
    }
}
