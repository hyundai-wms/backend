package com.myme.mywarehome.domains.issue.application.service;

import com.myme.mywarehome.domains.issue.application.domain.Issue;
import com.myme.mywarehome.domains.issue.application.port.in.GetAllIssueUseCase;
import com.myme.mywarehome.domains.issue.application.port.in.command.GetAllIssueCommand;
import com.myme.mywarehome.domains.issue.application.port.out.GetAllIssuePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllIssueService implements GetAllIssueUseCase {
    private final GetAllIssuePort getAllIssuePort;


    @Override
    public Page<Issue> getAllIssue(GetAllIssueCommand command, Pageable pageable) {
        return getAllIssuePort.findAllIssues(command, pageable);
    }
}
