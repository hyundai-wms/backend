package com.myme.mywarehome.domains.issue.adapter.in.web.response;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;

import com.myme.mywarehome.infrastructure.util.helper.DateFormatHelper;
import java.time.LocalDateTime;

public record IssuePlanResponse(
        Long issuePlanId,
        String issuePlanCode,
        String issuePlanDate,
        String productNumber,
        String productName,
        Integer itemCount,
        Long companyId,
        String companyCode,
        String companyName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
    public static IssuePlanResponse of(IssuePlan issuePlan) {
        return new IssuePlanResponse(
                issuePlan.getIssuePlanId(),
                issuePlan.getIssuePlanCode(),
                DateFormatHelper.formatDate(issuePlan.getIssuePlanDate()),
                issuePlan.getProduct().getProductNumber(),
                issuePlan.getProduct().getProductName(),
                issuePlan.getIssuePlanItemCount(),
                issuePlan.getProduct().getCompany().getCompanyId(),
                issuePlan.getProduct().getCompany().getCompanyCode(),
                issuePlan.getProduct().getCompany().getCompanyName(),
                issuePlan.getCreatedAt(),
                issuePlan.getUpdatedAt()
        );
    }
}


