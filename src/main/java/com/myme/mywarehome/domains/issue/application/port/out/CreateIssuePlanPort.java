package com.myme.mywarehome.domains.issue.application.port.out;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;

import java.util.List;

public interface CreateIssuePlanPort {
    IssuePlan create(IssuePlan issuePlan);
    List<IssuePlan> createBulk(List<IssuePlan> issuePlanList);
}
