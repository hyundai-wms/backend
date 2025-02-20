package com.myme.mywarehome.domains.stock.application.port.in.result;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record StockSummaryResult(
        String productNumber,
        String productName,
        Long totalItemCount,
        String totalItemCountTrend,
        Integer eachCount,
        Long safeItemCount,
        Long recentReceiptId,
        String recentReceiptCode,
        String recentReceiptDate,
        Long upcomingIssuePlanId,
        String upcomingIssuePlanCode,
        String upcomingIssuePlanDate,
        Long companyId,
        String companyCode,
        String companyName,
        String bayNumberSummary,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
