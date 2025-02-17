package com.myme.mywarehome.domains.issue.application.port.in;

import com.myme.mywarehome.domains.issue.application.port.in.result.TodayIssueResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface GetTodayIssueUseCase {
    Page<TodayIssueResult> getTodayIssue(LocalDate selectedDate, Pageable pageable);
}
