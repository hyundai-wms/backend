package com.myme.mywarehome.domains.mrp.application.port.in.command;

import java.time.LocalDate;
import java.util.Map;

public record MrpInputCommand(
        Map<String, Integer> engineCountMap,
        LocalDate dueDate
) {

}
