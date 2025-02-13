package com.myme.mywarehome.domains.issue.adapter.out;

import com.myme.mywarehome.domains.issue.adapter.out.persistence.IssuePlanJpaRepository;
import com.myme.mywarehome.domains.issue.application.port.out.DeleteIssuePlanPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteIssuePlanAdapter implements DeleteIssuePlanPort {
    private final IssuePlanJpaRepository issuePlanJpaRepository;

    @Override
    public void delete(Long issuePlanId) {issuePlanJpaRepository.deleteById(issuePlanId);}

}
