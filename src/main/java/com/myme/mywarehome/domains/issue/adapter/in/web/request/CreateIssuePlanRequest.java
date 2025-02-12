package com.myme.mywarehome.domains.issue.adapter.in.web.request;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.infrastructure.util.helper.DateFormatHelper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record CreateIssuePlanRequest(
        @NotBlank(message = "P/N은 필수입니다.")
        @Pattern(regexp = "^\\d{5}-\\d{2}P\\d{2}$", message = "P/N 형식이 유효하지 않습니다.(00000-00P00)")
        String productNumber,

        @NotNull(message = "수량은 필수입니다.")
        @Positive(message = "수량은 1 이상이어야 합니다")
        Integer itemCount,

        @NotBlank(message = "입고예정일은 필수입니다.")
        @Pattern(regexp = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])", message = "날짜 형식이 올바르지 않습니다")
        String issuePlanDate
) {
    // Todo : command로 변경할 수 있으면 해라
    public IssuePlan toEntity(Product product) {
        return IssuePlan.builder()
                .product(product)
                .issuePlanItemCount(this.itemCount)
                .issuePlanDate(DateFormatHelper.parseDate(this.issuePlanDate))
                .build();
    }
}
