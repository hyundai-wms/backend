package com.myme.mywarehome.domains.receipt.application.port.in.event;

import com.myme.mywarehome.domains.receipt.application.domain.Receipt;
import com.myme.mywarehome.domains.stock.application.domain.Stock;
import java.util.concurrent.CompletableFuture;

public record ReceiptCreatedEvent(
        Receipt receipt,
        CompletableFuture<Stock> result
) {
}
