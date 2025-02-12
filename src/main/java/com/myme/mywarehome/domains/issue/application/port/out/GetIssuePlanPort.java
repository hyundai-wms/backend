package com.myme.mywarehome.domains.issue.application.port.out;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import java.util.Optional;

public interface GetIssuePlanPort {
    Optional<IssuePlan> getIssuePlanById(Long issuePlanId);
    boolean existsIssuePlanById(Long issuePlanId);

}
