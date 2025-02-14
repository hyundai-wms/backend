package com.myme.mywarehome.domains.receipt.application.port.in.event;

import com.myme.mywarehome.domains.receipt.application.domain.Receipt;

import java.util.List;

public record ReceiptBulkCreatedEvent(
    List<Receipt> receiptList
) {
}
