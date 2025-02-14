package com.myme.mywarehome.domains.issue.application.port.in.command;

import java.time.LocalDate;

public record GetAllIssueCommand(
        String companyCode,
        String companyName,
        String issuePlanCode,
        LocalDate issuePlanStartDate,
        LocalDate issuePlanEndDate,
        String productNumber,
        String productName,
        String issueCode,
        LocalDate issueStartDate,
        LocalDate issueEndDate
) {
}
