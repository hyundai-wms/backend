package com.myme.mywarehome.domains.issue.application.port.out;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;

import java.util.Optional;

public interface UpdateIssuePlanPort {
    IssuePlan update(IssuePlan issuePlan);
    Optional<IssuePlan> findById(Long id);

}
