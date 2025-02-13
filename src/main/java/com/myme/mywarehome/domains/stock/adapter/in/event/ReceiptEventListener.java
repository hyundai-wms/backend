package com.myme.mywarehome.domains.stock.adapter.in.event;

import com.myme.mywarehome.domains.receipt.application.port.in.event.ReceiptCreatedEvent;
import com.myme.mywarehome.domains.stock.application.domain.Stock;
import com.myme.mywarehome.domains.stock.application.port.in.CreateStockUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

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
}
