package com.myme.mywarehome.domains.issue.adapter.out;

import com.myme.mywarehome.domains.issue.adapter.out.persistence.IssueJpaRepository;
import com.myme.mywarehome.domains.issue.application.port.out.GetIssuePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetIssueAdapter implements GetIssuePort {
    private final IssueJpaRepository issueJpaRepository;


    @Override
    public boolean existsByStockId(Long stockId) {
        return issueJpaRepository.existsByStockId(stockId);
    }

    @Override
    public long countProcessedIssueByIssuePlanId(Long issuePlanId) {
        return issueJpaRepository.countProcessedIssueByIssuePlanId(issuePlanId);
    }
}
