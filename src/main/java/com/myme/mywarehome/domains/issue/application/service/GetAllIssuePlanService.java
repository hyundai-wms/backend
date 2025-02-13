package com.myme.mywarehome.domains.issue.application.service;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.in.GetAllIssuePlanUseCase;
import com.myme.mywarehome.domains.issue.application.port.in.command.GetAllIssuePlanCommand;
import com.myme.mywarehome.domains.issue.application.port.out.GetIssuePlanPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllIssuePlanService implements GetAllIssuePlanUseCase {
    private final GetIssuePlanPort getIssuePlanPort;

    @Override
    public Page<IssuePlan> getAllIssuePlan(GetAllIssuePlanCommand command, Pageable pageable) {
        return getIssuePlanPort.findAllIssuePlans(command, pageable);
    }

}
