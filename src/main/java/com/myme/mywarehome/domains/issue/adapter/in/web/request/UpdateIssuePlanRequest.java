package com.myme.mywarehome.domains.issue.adapter.in.web.request;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.product.application.domain.Product;

public record UpdateIssuePlanRequest(
        // Todo: 재고 id 사용해서 itemCount 세어야 함
        // Todo : companyCode 필요
        String productNumber,
        //Integer itemCount,
        // companyCode 추가 필요
        String issuePlanDate
) {
    public IssuePlan toEntity(Long issuePlanId, Product product) {
        return IssuePlan.builder()
                .issuePlanId(issuePlanId)
                .product(product)
                .issuePlanDate(this.issuePlanDate)
                .build();

    }
}
