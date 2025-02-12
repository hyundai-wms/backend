package com.myme.mywarehome.domains.receipt.adapter.in.web.request;

import com.myme.mywarehome.domains.receipt.application.port.in.command.ReceiptPlanCommand;
import com.myme.mywarehome.infrastructure.util.helper.DateFormatHelper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record UpdateReceiptPlanRequest(
        @Pattern(regexp = "^\\d{5}-\\d{2}P\\d{2}$", message = "P/N 형식이 유효하지 않습니다.(00000-00P00)")
        String productNumber,

        @Positive(message = "수량은 1 이상이어야 합니다")
        Integer itemCount,

        @Pattern(regexp = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])", message = "날짜 형식이 올바르지 않습니다")
        String receiptPlanDate
) {
    public ReceiptPlanCommand toCommand() {
        return new ReceiptPlanCommand(
                this.productNumber,
                this.itemCount,
                this.receiptPlanDate == null ? null : DateFormatHelper.parseDate(this.receiptPlanDate)
        );
    }
}
