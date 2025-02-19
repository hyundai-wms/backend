package com.myme.mywarehome.domains.issue.adapter.out.event;

import com.myme.mywarehome.domains.issue.application.port.in.result.TodayIssueResult;

import java.time.LocalDate;

public record TodayIssueEvent(
        String type,
        TodayIssueResult data,
        LocalDate date
) {
}
