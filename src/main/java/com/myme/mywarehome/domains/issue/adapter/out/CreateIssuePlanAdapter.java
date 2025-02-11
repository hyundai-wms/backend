package com.myme.mywarehome.domains.issue.adapter.out;

import com.myme.mywarehome.domains.issue.adapter.out.persistence.IssuePlanJpaRepository;
import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.out.CreateIssuePlanPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CreateIssuePlanAdapter implements CreateIssuePlanPort {
    private final IssuePlanJpaRepository issuePlanJpaRepository;

    @Override
    public IssuePlan create(IssuePlan issuePlan) { return issuePlanJpaRepository.save(issuePlan); }

    @Override
    public List<IssuePlan> createBulk(List<IssuePlan> issuePlanList) {
        return issuePlanJpaRepository.saveAll(issuePlanList);
    }
}
