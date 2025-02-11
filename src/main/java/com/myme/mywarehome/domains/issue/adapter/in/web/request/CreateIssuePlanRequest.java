package com.myme.mywarehome.domains.issue.adapter.in.web.request;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.product.application.domain.Product;

public record CreateIssuePlanRequest(
        // Todo: 재고 id 사용해서 itemCount 세어야 함
        // Todo: companyId companyCode companyName 필요
        String productNumber,
        //Integer itemCount, 일단 재고 빼고 재고id를 통틀었는데 거기에다 필터링까지??? 멘붕
        String issuePlanDate
) {
    public IssuePlan toEntity(Product product) {
        return IssuePlan.builder()
                .product(product)
                .issuePlanDate(this.issuePlanDate)
                .build();
    }
}
