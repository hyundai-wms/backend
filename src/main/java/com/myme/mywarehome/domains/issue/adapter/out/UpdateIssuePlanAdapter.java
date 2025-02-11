package com.myme.mywarehome.domains.issue.adapter.out;

import com.myme.mywarehome.domains.issue.adapter.out.persistence.IssuePlanJpaRepository;
import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.out.UpdateIssuePlanPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UpdateIssuePlanAdapter implements UpdateIssuePlanPort {
    private final IssuePlanJpaRepository issuePlanJpaRepository;

    @Override
    public IssuePlan update(IssuePlan issuePlan) { return issuePlanJpaRepository.save(issuePlan); }

    @Override
    public Optional<IssuePlan> findById(Long id) {
        return issuePlanJpaRepository.findById(id);
    }
}
