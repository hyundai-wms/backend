package com.myme.mywarehome.domains.issue.adapter.in.web.response;

import com.myme.mywarehome.domains.issue.application.domain.Issue;

import java.time.LocalDateTime;

public record IssueResponse(
        Long issueId,
        String issueCode,
        String issueDate,
        Long stockId,
        String stockCode,
        String productNumber,
        String productName,
        Integer eachCount,
        Long issuePlanId,
        String issuePlanCode,
        String issuePlanDate,
        Long companyId,
        String companyCode,
        String companyName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
    public static IssueResponse from(Issue issue) {
        return new IssueResponse(
                issue.getIssueId(),
                issue.getIssueCode(),
                issue.getIssueDate().toString(),
                issue.getStock().getStockId(),  // stockId 사용
                issue.getStock().getStockCode(),
                issue.getIssuePlan().getProduct().getProductNumber(),
                issue.getIssuePlan().getProduct().getProductName(),
                issue.getIssuePlan().getProduct().getEachCount(),
                issue.getIssuePlan().getIssuePlanId(),
                issue.getIssuePlan().getIssuePlanCode(),
                issue.getIssuePlan().getIssuePlanDate().toString(),
                issue.getIssuePlan().getProduct().getCompany().getCompanyId(),
                issue.getIssuePlan().getProduct().getCompany().getCompanyCode(),
                issue.getIssuePlan().getProduct().getCompany().getCompanyName(),
                issue.getCreatedAt(),
                issue.getUpdatedAt()
        );
    }
}


