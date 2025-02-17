package com.myme.mywarehome.domains.receipt.application.port.in.event;

import java.time.LocalDate;
import java.util.List;

public record ReceiptPlanBulkStatusChangedEvent(
        List<Long> receiptPlanIds,
        LocalDate selectedDate,
        String status
) {}