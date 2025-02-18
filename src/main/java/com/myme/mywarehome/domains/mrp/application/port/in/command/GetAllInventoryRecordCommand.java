package com.myme.mywarehome.domains.mrp.application.port.in.command;

import java.time.LocalDate;

public record GetAllInventoryRecordCommand(
        LocalDate startDate,
        LocalDate endDate
) {

}
