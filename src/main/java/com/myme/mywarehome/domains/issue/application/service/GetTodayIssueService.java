package com.myme.mywarehome.domains.issue.application.service;

import com.myme.mywarehome.domains.issue.application.port.in.GetTodayIssueUseCase;
import com.myme.mywarehome.domains.issue.application.port.in.result.TodayIssueResult;
import com.myme.mywarehome.domains.issue.application.port.out.GetIssuePlanPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GetTodayIssueService implements GetTodayIssueUseCase {
    private final GetIssuePlanPort getIssuePlanPort;


    @Override
    public Page<TodayIssueResult> getTodayIssue(LocalDate selectedDate, Pageable pageable) {
        return getIssuePlanPort.findTodayIssues(selectedDate, pageable);
    }
}
