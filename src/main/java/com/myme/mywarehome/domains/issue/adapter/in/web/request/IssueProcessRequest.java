package com.myme.mywarehome.domains.issue.adapter.in.web.request;

import com.myme.mywarehome.domains.issue.application.port.in.command.IssueProcessCommand;
import com.myme.mywarehome.infrastructure.util.helper.DateFormatHelper;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record IssueProcessRequest(
        Long issuePlanId
) {
    public IssueProcessCommand toCommand() {
        return new IssueProcessCommand(issuePlanId);

    }
}
