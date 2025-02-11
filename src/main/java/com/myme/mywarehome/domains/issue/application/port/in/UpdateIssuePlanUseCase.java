package com.myme.mywarehome.domains.issue.application.port.in;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;

public interface UpdateIssuePlanUseCase {
    IssuePlan update(IssuePlan issuePlan);
}
