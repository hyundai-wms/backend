package com.myme.mywarehome.domains.issue.adapter.out;

import com.myme.mywarehome.domains.issue.adapter.out.persistence.IssuePlanJpaRepository;
import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.in.command.GetAllIssuePlanCommand;
import com.myme.mywarehome.domains.issue.application.port.out.GetIssuePlanPort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetIssuePlanAdapter implements GetIssuePlanPort {
    private final IssuePlanJpaRepository issuePlanJpaRepository;

    @Override
    public Optional<IssuePlan> getIssuePlanById(Long issuePlanId) {
        return issuePlanJpaRepository.findById(issuePlanId);
    }

    @Override
    public boolean existsIssuePlanById(Long issuePlanId) {
        return issuePlanJpaRepository.existsById(issuePlanId);
    }

    @Override
    public Page<IssuePlan> findAllIssuePlans(GetAllIssuePlanCommand command, Pageable pageable) {
        return issuePlanJpaRepository.findByConditions(
                command.companyName(),
                command.productName(),
                command.companyCode(),
                command.issuePlanStartDate(),
                command.issuePlanEndDate(),
                command.productName(),
                command.issuePlanCode(),
                pageable
        );
    }



}
