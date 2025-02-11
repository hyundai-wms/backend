package com.myme.mywarehome.domains.issue.adapter.in.web.response;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;

import java.time.LocalDateTime;

// Todo: itemCount, companyId, companyCode, companyName 추가
public record CreateIssuePlanResponse(
        Long issuePlanId,
        String issuePlanCode,
        String issuePlanDate,
        String productNumber,
        String productName,
//        Integer itemCount,
//        String companyCode,
//        String companyName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
    public static CreateIssuePlanResponse of(IssuePlan issuePlan) {
        return new CreateIssuePlanResponse(
                issuePlan.getIssuePlanId(),
                issuePlan.getIssuePlanCode(),
                issuePlan.getIssuePlanDate(),
                issuePlan.getProduct().getProductNumber(),  // Product에서 가져옴
                issuePlan.getProduct().getProductName(),    // Product
                issuePlan.getCreatedAt(),
                issuePlan.getUpdatedAt()
        );
    }
}
