package com.myme.mywarehome.domains.issue.application.service;

import com.myme.mywarehome.domains.issue.adapter.out.exception.IssuePlanNotFoundException;
import com.myme.mywarehome.domains.issue.application.port.in.DeleteIssuePlanUseCase;
import com.myme.mywarehome.domains.issue.application.port.out.DeleteIssuePlanPort;
import com.myme.mywarehome.domains.issue.application.port.out.GetIssuePlanPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteIssuePlanService implements DeleteIssuePlanUseCase {
    private final GetIssuePlanPort getIssuePlanPort;
    private final DeleteIssuePlanPort deleteIssuePlanPort;

    @Override
    public void deleteIssuePlan(Long issuePlanId) {
        if(!getIssuePlanPort.existsIssuePlanById(issuePlanId)) {
            throw new IssuePlanNotFoundException();
        }

        deleteIssuePlanPort.delete(issuePlanId);
    }

}
