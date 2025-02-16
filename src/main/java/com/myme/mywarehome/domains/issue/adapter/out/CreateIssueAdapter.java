package com.myme.mywarehome.domains.issue.adapter.out;

import com.myme.mywarehome.domains.issue.adapter.out.persistence.IssueJpaRepository;
import com.myme.mywarehome.domains.issue.application.domain.Issue;
import com.myme.mywarehome.domains.issue.application.port.out.CreateIssuePort;
import com.myme.mywarehome.domains.receipt.application.domain.Receipt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateIssueAdapter implements CreateIssuePort {
    private final IssueJpaRepository issueJpaRepository;

    @Override
    public Issue create(Issue issue) {
        return issueJpaRepository.save(issue);
    }
}
