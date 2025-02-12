package com.myme.mywarehome.domains.issue.adapter.in.web.response;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;

import com.myme.mywarehome.infrastructure.util.helper.DateFormatHelper;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Todo: itemCount, companyId, companyCode, companyName 추가
public record CreateIssuePlanResponse(
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
    public static CreateIssuePlanResponse of(IssuePlan issuePlan) {
        return new CreateIssuePlanResponse(
                issuePlan.getIssuePlanId(),
                issuePlan.getIssuePlanCode(),
                DateFormatHelper.formatDate(issuePlan.getIssuePlanDate()),
                issuePlan.getProduct().getProductNumber(),  // Product에서 가져옴
                issuePlan.getProduct().getProductName(),    // Product
                issuePlan.getIssuePlanItemCount(),
                issuePlan.getProduct().getCompany().getCompanyId(),
                issuePlan.getProduct().getCompany().getCompanyCode(),
                issuePlan.getProduct().getCompany().getCompanyName(),
                issuePlan.getCreatedAt(),
                issuePlan.getUpdatedAt()
        );
    }
}
