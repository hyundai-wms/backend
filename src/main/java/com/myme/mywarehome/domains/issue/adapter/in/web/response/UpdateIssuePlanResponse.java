package com.myme.mywarehome.domains.issue.adapter.in.web.response;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;

import java.time.LocalDateTime;
// Todo: itemCount, companyId, companyCode, companyName 추가
public record UpdateIssuePlanResponse(
        Long issuePlanId,
        String issuePlanCode,
        String issuePlanDate,
        String productNumber,
        String productName,
        // Integer itemCount,
        // String companyId,
        // String companyCode,
        // String companyName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
    public static UpdateIssuePlanResponse of(IssuePlan issuePlan) {
        return new UpdateIssuePlanResponse(
                issuePlan.getIssuePlanId(),
                issuePlan.getIssuePlanCode(),
                issuePlan.getIssuePlanDate(),
                issuePlan.getProduct().getProductNumber(),
                issuePlan.getProduct().getProductName(),
                issuePlan.getCreatedAt(),
                issuePlan.getUpdatedAt()
        );
    }
}


