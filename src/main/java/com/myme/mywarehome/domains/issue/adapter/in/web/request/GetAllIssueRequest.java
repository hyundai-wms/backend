package com.myme.mywarehome.domains.issue.adapter.in.web.request;

import com.myme.mywarehome.domains.issue.application.port.in.command.GetAllIssueCommand;
import com.myme.mywarehome.infrastructure.util.helper.DateFormatHelper;
import jakarta.validation.constraints.Pattern;

public record GetAllIssueRequest(
        String companyCode,
        String companyName,
        String issuePlanCode,
        @Pattern(regexp = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])", message = "날짜 형식이 올바르지 않습니다")
        String issuePlanStartDate,
        @Pattern(regexp = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])", message = "날짜 형식이 올바르지 않습니다")
        String issuePlanEndDate,
        String productNumber,
        String productName,
        String issueCode,
        @Pattern(regexp = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])", message = "날짜 형식이 올바르지 않습니다")
        String issueStartDate,
        @Pattern(regexp = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])", message = "날짜 형식이 올바르지 않습니다")
        String issueEndDate
) {
    public GetAllIssueCommand toCommand() {
        return new GetAllIssueCommand(
                this.companyCode,
                this.companyName,
                this.issuePlanCode,
                this.issuePlanStartDate == null ? null
                        : DateFormatHelper.parseDate(this.issuePlanStartDate),
                this.issuePlanEndDate == null ? null
                        : DateFormatHelper.parseDate(this.issuePlanEndDate),
                this.productNumber,
                this.productName,
                this.issueCode,
                this.issueStartDate == null ? null
                        : DateFormatHelper.parseDate(this.issueStartDate),
                this.issueEndDate == null ? null
                        : DateFormatHelper.parseDate(this.issueEndDate)
                
        );
    }
}
