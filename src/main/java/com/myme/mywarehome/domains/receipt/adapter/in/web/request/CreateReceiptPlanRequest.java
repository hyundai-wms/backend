package com.myme.mywarehome.domains.receipt.adapter.in.web.request;

import com.myme.mywarehome.domains.receipt.application.port.in.command.ReceiptPlanCommand;
import com.myme.mywarehome.infrastructure.util.helper.DateFormatHelper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record CreateReceiptPlanRequest(
        @NotBlank(message = "P/N은 필수입니다.")
        @Pattern(regexp = "^\\d{5}-\\d{2}P\\d{2}$", message = "P/N 형식이 유효하지 않습니다.(00000-00P00)")
        String productNumber,

        @NotNull(message = "수량은 필수입니다.")
        @Positive(message = "수량은 1 이상이어야 합니다")
        Integer itemCount,

        @NotBlank(message = "입고예정일은 필수입니다.")
        @Pattern(regexp = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])", message = "날짜 형식이 올바르지 않습니다")
        String receiptPlanDate
) {
    public ReceiptPlanCommand toCommand() {
        return new ReceiptPlanCommand(
                this.productNumber,
                this.itemCount,
                DateFormatHelper.parseDate(this.receiptPlanDate)
        );
    }
}
