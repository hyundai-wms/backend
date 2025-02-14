package com.myme.mywarehome.domains.issue.application.port.out;

import com.myme.mywarehome.domains.issue.application.domain.Issue;
import com.myme.mywarehome.domains.issue.application.port.in.command.GetAllIssueCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetAllIssuePort {
    Page<Issue> findAllIssues(GetAllIssueCommand command, Pageable pageable);
}
