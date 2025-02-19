package com.myme.mywarehome.domains.mrp.application.port.in.command;

import java.time.LocalDate;

public record GetAllMrpOutputCommand(
        LocalDate startDate,
        LocalDate endDate,
        Boolean isOrdered
) {

}
