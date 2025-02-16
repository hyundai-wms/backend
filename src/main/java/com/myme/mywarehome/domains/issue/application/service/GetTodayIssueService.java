package com.myme.mywarehome.domains.issue.application.service;

import com.myme.mywarehome.domains.issue.application.port.in.GetTodayIssueUseCase;
import com.myme.mywarehome.domains.issue.application.port.in.command.SelectedDateCommand;
import com.myme.mywarehome.domains.issue.application.port.in.result.TodayIssueResult;
import com.myme.mywarehome.domains.issue.application.port.out.GetIssuePlanPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetTodayIssueService implements GetTodayIssueUseCase {
    private final GetIssuePlanPort getIssuePlanPort;


    @Override
    public Page<TodayIssueResult> getTodayIssue(SelectedDateCommand command, Pageable pageable) {
        return getIssuePlanPort.findTodayIssues(command.selectedDate(), pageable);
    }
}
