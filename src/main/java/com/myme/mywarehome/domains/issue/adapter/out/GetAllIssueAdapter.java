package com.myme.mywarehome.domains.issue.adapter.out;

import com.myme.mywarehome.domains.issue.adapter.out.persistence.IssueJpaRepository;
import com.myme.mywarehome.domains.issue.application.domain.Issue;
import com.myme.mywarehome.domains.issue.application.port.in.command.GetAllIssueCommand;
import com.myme.mywarehome.domains.issue.application.port.out.GetAllIssuePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetAllIssueAdapter implements GetAllIssuePort {
    private final IssueJpaRepository issueJpaRepository;


    @Override
    public Page<Issue> findAllIssues(GetAllIssueCommand command, Pageable pageable) {
        return issueJpaRepository.findByConditions(
                command.companyCode(),
                command.companyName(),
                command.issuePlanCode(),
                command.issuePlanStartDate(),
                command.issuePlanEndDate(),
                command.productNumber(),
                command.productName(),
                command.issueCode(),
                command.issueStartDate(),
                command.issueEndDate(),
                pageable
        );
    }
}
