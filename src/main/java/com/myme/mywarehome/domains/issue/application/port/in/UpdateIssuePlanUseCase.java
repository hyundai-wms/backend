package com.myme.mywarehome.domains.issue.application.port.in;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.in.command.IssuePlanCommand;

public interface UpdateIssuePlanUseCase {
    IssuePlan update(Long issuePlanId, IssuePlanCommand command);
}
