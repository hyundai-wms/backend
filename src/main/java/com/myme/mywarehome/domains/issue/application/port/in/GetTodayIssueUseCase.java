package com.myme.mywarehome.domains.issue.application.port.in;

import com.myme.mywarehome.domains.issue.application.port.in.command.SelectedDateCommand;
import com.myme.mywarehome.domains.issue.application.port.in.result.TodayIssueResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetTodayIssueUseCase {
    Page<TodayIssueResult> getTodayIssue(SelectedDateCommand command, Pageable pageable);
}
