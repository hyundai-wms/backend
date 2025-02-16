package com.myme.mywarehome.domains.issue.adapter.in.web.response;

import com.myme.mywarehome.domains.issue.application.domain.Issue;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record IssueProcessResponse(
        Long issueId,
        String issueCode,
        LocalDate issueDate,
        String productNumber,
        String productName,
        Integer eachCount,
        Long issuePlanId,
        String issuePlanCode,
        LocalDate issuePlanDate,
        Long companyId,
        String companyCode,
        String companyName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static IssueProcessResponse from(Issue issue) {
        return new IssueProcessResponse(
                issue.getIssueId(),
                issue.getIssueCode(),
                issue.getIssueDate(),
                issue.getProduct().getProductNumber(),
                issue.getProduct().getProductName(),
                issue.getProduct().getEachCount(),
                issue.getIssuePlan().getIssuePlanId(),
                issue.getIssuePlan().getIssuePlanCode(),
                issue.getIssuePlan().getIssuePlanDate(),
                issue.getProduct().getCompany().getCompanyId(),
                issue.getProduct().getCompany().getCompanyCode(),
                issue.getProduct().getCompany().getCompanyName(),
                issue.getCreatedAt(),
                issue.getUpdatedAt()
        );
    }
}




