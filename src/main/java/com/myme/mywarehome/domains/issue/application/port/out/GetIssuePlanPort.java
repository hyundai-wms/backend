package com.myme.mywarehome.domains.issue.application.port.out;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.in.command.GetAllIssuePlanCommand;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetIssuePlanPort {
    Optional<IssuePlan> getIssuePlanById(Long issuePlanId);
    Page<IssuePlan> findAllIssuePlans (GetAllIssuePlanCommand command, Pageable pageable);
    boolean existsIssuePlanById(Long issuePlanId);

}
