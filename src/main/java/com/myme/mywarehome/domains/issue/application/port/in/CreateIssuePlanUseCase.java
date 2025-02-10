package com.myme.mywarehome.domains.issue.application.port.in;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;

import java.util.List;

public interface CreateIssuePlanUseCase {
    IssuePlan create(IssuePlan issuePlan);
    List<IssuePlan> createBulk(List<IssuePlan> issuePlans);
}
