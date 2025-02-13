package com.myme.mywarehome.domains.issue.adapter.in.web.request;

import com.myme.mywarehome.domains.issue.application.port.in.command.GetAllIssuePlanCommand;
import com.myme.mywarehome.infrastructure.util.helper.DateFormatHelper;
import jakarta.validation.constraints.Pattern;

public record GetAllIssuePlanRequest(
        String companyCode,
        String companyName,
        @Pattern(regexp = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])", message = "날짜 형식이 올바르지 않습니다")
        String issuePlanStartDate,
        @Pattern(regexp = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])", message = "날짜 형식이 올바르지 않습니다")
        String issuePlanEndDate,
        String productNumber,
        String productName,
        String issuePlanCode

) {
    public GetAllIssuePlanCommand toCommand() {
        return new GetAllIssuePlanCommand(
                this.companyCode,
                this.companyName,
                this.issuePlanStartDate == null ? null
                        : DateFormatHelper.parseDate(this.issuePlanStartDate),
                this.issuePlanEndDate == null ? null
                        : DateFormatHelper.parseDate(this.issuePlanEndDate),
                this.productNumber,
                this.productName,
                this.issuePlanCode
        );
    }

}
