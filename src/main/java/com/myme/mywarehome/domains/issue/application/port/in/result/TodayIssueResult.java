package com.myme.mywarehome.domains.issue.application.port.in.result;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TodayIssueResult(
        Long issuePlanId,
        String issuePlanCode,
        LocalDate issuePlanDate,
        Long issueCount,
        Long totalItemCount,
        String issueStatus,
        String productNumber,
        String productName,
        Long companyId,
        String companyCode,
        String companyName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}



