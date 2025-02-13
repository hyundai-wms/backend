package com.myme.mywarehome.domains.issue.adapter.out;

import com.myme.mywarehome.domains.issue.adapter.out.persistence.IssuePlanJpaRepository;
import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.out.GetIssuePlanPort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetIssuePlanAdapter implements GetIssuePlanPort {
    private final IssuePlanJpaRepository issuePlanJpaRepository;

    @Override
    public Optional<IssuePlan> getIssuePlanById(Long issuePlanId) {
        return issuePlanJpaRepository.findById(issuePlanId);
    }

    @Override
    public boolean existsIssuePlanById(Long issuePlanId) {
        return issuePlanJpaRepository.existsById(issuePlanId);
    }


}
