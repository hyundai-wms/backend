package com.myme.mywarehome.domains.receipt.application.port.in.event;

import java.time.LocalDate;

public record ReceiptPlanStatusChangedEvent(
        Long receiptPlanId,
        LocalDate selectedDate,
        String eventType
) {

}