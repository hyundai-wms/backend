package com.myme.mywarehome.domains.issue.adapter.out;

import com.myme.mywarehome.domains.issue.adapter.out.persistence.IssuePlanJpaRepository;
import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.out.UpdateIssuePlanPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateIssuePlanAdapter implements UpdateIssuePlanPort {

    private final IssuePlanJpaRepository issuePlanJpaRepository;

    @Override
    public Optional<IssuePlan> update(IssuePlan issuePlan) {
        if (issuePlan.getIssuePlanId() != null) {
            return Optional.of(issuePlanJpaRepository.save(issuePlan));
        } else {
            log.error("IssuePlan Not Changed : IssuePlan Id is null");
            return Optional.empty();
        }
    }
}