package com.myme.mywarehome.domains.receipt.application.port.in.result;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TodayReceiptResult(
        Long receiptPlanId,
        String receiptPlanCode,
        LocalDate receiptPlanDate,
        Long receiptCount,
        Long itemCount,
        String receiptStatus,
        String productNumber,
        String productName,
        Long companyId,
        String companyCode,
        String companyName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
