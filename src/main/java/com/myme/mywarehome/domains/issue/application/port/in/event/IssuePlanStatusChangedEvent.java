package com.myme.mywarehome.domains.issue.application.port.in.event;

import java.time.LocalDate;

public record IssuePlanStatusChangedEvent(
        Long issuePlanId,
        LocalDate selectedDate,
        String eventType
) {
}
