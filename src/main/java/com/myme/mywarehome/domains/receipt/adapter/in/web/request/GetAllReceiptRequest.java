package com.myme.mywarehome.domains.receipt.adapter.in.web.request;

import com.myme.mywarehome.domains.receipt.application.port.in.command.GetAllReceiptCommand;
import com.myme.mywarehome.infrastructure.util.helper.DateFormatHelper;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.Date;

public record GetAllReceiptRequest(
        String companyCode,
        String companyName,
        String receiptPlanCode,
        @Pattern(regexp = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])", message = "날짜 형식이 올바르지 않습니다")
        String receiptPlanStartDate,
        @Pattern(regexp = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])", message = "날짜 형식이 올바르지 않습니다")
        String receiptPlanEndDate,
        String productNumber,
        String productName,
        String receiptCode,
        @Pattern(regexp = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])", message = "날짜 형식이 올바르지 않습니다")
        String receiptStartDate,
        @Pattern(regexp = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])", message = "날짜 형식이 올바르지 않습니다")
        String receiptEndDate
) {
    public GetAllReceiptCommand toCommand() {
        return new GetAllReceiptCommand(
                this.companyCode,
                this.companyName,
                this.receiptPlanCode,
                this.receiptPlanStartDate == null ? null
                        : DateFormatHelper.parseDate(this.receiptPlanStartDate),
                this.receiptPlanEndDate == null ? null
                        : DateFormatHelper.parseDate(this.receiptPlanEndDate),
                this.productNumber,
                this.productName,
                this.receiptCode,
                this.receiptStartDate == null ? null
                        : DateFormatHelper.parseDate(this.receiptStartDate),
                this.receiptEndDate == null ? null
                        : DateFormatHelper.parseDate(this.receiptEndDate)
        );
    }
}
