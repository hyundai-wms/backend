package com.myme.mywarehome.domains.stock.application.port.in.command;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record StockSummaryCommand(
        String companyCode,
        String companyName,
        LocalDate recentReceiptStartDate,
        LocalDate recentReceiptEndDate,
        LocalDate upcomingIssuePlanStartDate,
        LocalDate upcomingIssuePlanEndDate,
        String productNumber,
        String productName
) {
}
