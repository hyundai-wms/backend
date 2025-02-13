package com.myme.mywarehome.domains.issue.application.port.in;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.in.command.GetAllIssuePlanCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetAllIssuePlanUseCase {
    Page<IssuePlan> getAllIssuePlan(GetAllIssuePlanCommand command, Pageable pageable);

}
