package com.myme.mywarehome.domains.receipt.application.port.in.command;

import java.time.LocalDate;

public record GetAllReceiptPlanCommand(
        String companyCode,
        String companyName,
        LocalDate receiptPlanStartDate,
        LocalDate receiptPlanEndDate,
        String productNumber,
        String productName
) {
}
