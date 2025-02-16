package com.myme.mywarehome.domains.issue.application.port.in.command;

import java.time.LocalDate;

public record SelectedDateCommand(
        LocalDate selectedDate
) {
}
