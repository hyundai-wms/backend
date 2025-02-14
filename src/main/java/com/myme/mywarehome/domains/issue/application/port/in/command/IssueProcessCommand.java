package com.myme.mywarehome.domains.issue.application.port.in.command;
import java.time.LocalDate;

public record IssueProcessCommand(
        LocalDate selectedDate,
        Long issuePlanId
) {
}
