package com.myme.mywarehome.domains.issue.application.port.in.command;

import java.time.LocalDate;

public record GetAllIssuePlanCommand(
        String companyCode,
        String companyName,
        LocalDate issuePlanStartDate,
        LocalDate issuePlanEndDate,
        String productNumber,
        String productName,
        String issuePlanCode

) {

}
