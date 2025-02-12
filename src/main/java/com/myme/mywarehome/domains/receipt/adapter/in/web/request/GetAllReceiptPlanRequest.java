package com.myme.mywarehome.domains.receipt.adapter.in.web.request;

import com.myme.mywarehome.domains.receipt.application.port.in.command.GetAllReceiptPlanCommand;
import com.myme.mywarehome.infrastructure.util.helper.DateFormatHelper;
import jakarta.validation.constraints.Pattern;

public record GetAllReceiptPlanRequest(
        String companyCode,
        String companyName,
        @Pattern(regexp = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])", message = "날짜 형식이 올바르지 않습니다")
        String receiptPlanStartDate,
        @Pattern(regexp = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])", message = "날짜 형식이 올바르지 않습니다")
        String receiptPlanEndDate,
        String productNumber,
        String productName
) {
    public GetAllReceiptPlanCommand toCommand() {
        return new GetAllReceiptPlanCommand(
                this.companyCode,
                this.companyName,
                this.receiptPlanStartDate == null ? null
                        : DateFormatHelper.parseDate(this.receiptPlanStartDate),
                this.receiptPlanEndDate == null ? null
                        : DateFormatHelper.parseDate(this.receiptPlanEndDate),
                this.productNumber,
                this.productName
        );
    }
}
