package com.myme.mywarehome.domains.issue.application.port.in;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;

import com.myme.mywarehome.domains.issue.application.port.in.command.IssuePlanCommand;
import java.util.List;

public interface CreateIssuePlanUseCase {
    IssuePlan create(IssuePlanCommand command);
    List<IssuePlan> createBulk(List<IssuePlanCommand> commandList);
}
