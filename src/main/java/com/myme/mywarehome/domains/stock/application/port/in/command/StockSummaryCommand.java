package com.myme.mywarehome.domains.stock.application.port.in.command;

import java.time.LocalDate;

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
