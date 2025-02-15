package com.myme.mywarehome.domains.receipt.application.port.in.command;

import java.time.LocalDate;

public record GetAllReceiptPlanCommand(
        String companyCode,
        String companyName,
        String receiptPlanCode,
        LocalDate receiptPlanStartDate,
        LocalDate receiptPlanEndDate,
        String productNumber,
        String productName
) {
}
