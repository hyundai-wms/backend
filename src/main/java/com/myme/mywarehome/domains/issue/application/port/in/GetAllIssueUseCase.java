package com.myme.mywarehome.domains.issue.application.port.in;

import com.myme.mywarehome.domains.issue.application.domain.Issue;
import com.myme.mywarehome.domains.issue.application.port.in.command.GetAllIssueCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetAllIssueUseCase {
    Page<Issue> getAllIssue(GetAllIssueCommand command, Pageable pageable);
}
