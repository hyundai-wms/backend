package com.myme.mywarehome.domains.issue.application.port.in;

import com.myme.mywarehome.domains.issue.application.domain.Issue;
import com.myme.mywarehome.domains.issue.application.port.in.command.IssueProcessCommand;

import java.time.LocalDate;

public interface IssueProcessUseCase {
    Issue process(Long stockId, IssueProcessCommand issueProcessCommand, LocalDate selectedDate);
}



