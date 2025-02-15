package com.myme.mywarehome.domains.stock.adapter.in.web.request;

import com.myme.mywarehome.domains.stock.application.port.in.command.StockSummaryCommand;
import com.myme.mywarehome.infrastructure.util.helper.DateFormatHelper;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record GetAllStockRequest(
        String companyCode,
        String companyName,
        @Pattern(regexp = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])", message = "날짜 형식이 올바르지 않습니다")
        String recentReceiptStartDate,
        @Pattern(regexp = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])", message = "날짜 형식이 올바르지 않습니다")
        String recentReceiptEndDate,
        @Pattern(regexp = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])", message = "날짜 형식이 올바르지 않습니다")
        String upcomingIssuePlanStartDate,
        @Pattern(regexp = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])", message = "날짜 형식이 올바르지 않습니다")
        String upcomingIssuePlanEndDate,
        String productNumber,
        String productName
) {
    public StockSummaryCommand toCommand() {
        return new StockSummaryCommand(
                this.companyCode,
                this.companyName,
                this.recentReceiptStartDate == null ? null
                        : DateFormatHelper.parseDate(this.recentReceiptStartDate),
                this.recentReceiptEndDate == null ? null
                        : DateFormatHelper.parseDate(this.recentReceiptEndDate),
                this.upcomingIssuePlanStartDate == null ? null
                        : DateFormatHelper.parseDate(upcomingIssuePlanStartDate),
                this.upcomingIssuePlanEndDate == null ? null
                        : DateFormatHelper.parseDate(upcomingIssuePlanEndDate),
                this.productNumber,
                this.productName
        );
    }
}
