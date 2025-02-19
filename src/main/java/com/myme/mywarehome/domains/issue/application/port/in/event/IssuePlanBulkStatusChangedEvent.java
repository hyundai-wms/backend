package com.myme.mywarehome.domains.issue.application.port.in.event;

import java.time.LocalDate;
import java.util.List;

public record IssuePlanBulkStatusChangedEvent(
        List<Long> issuePlanIds,
        LocalDate selectedDate,
        String status
) {
}
