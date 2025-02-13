package com.myme.mywarehome.domains.issue.application.port.in.command;

import java.time.LocalDate;

public record IssuePlanCommand(
        String productNumber,
        Integer itemCount,
        LocalDate issuePlanDate
) {

}
