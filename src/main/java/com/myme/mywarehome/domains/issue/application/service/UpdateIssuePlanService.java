package com.myme.mywarehome.domains.issue.application.service;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.in.UpdateIssuePlanUseCase;
import com.myme.mywarehome.domains.issue.application.port.out.UpdateIssuePlanPort;
import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.product.application.port.out.GetProductPort;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateIssuePlanService implements UpdateIssuePlanUseCase {
    private final UpdateIssuePlanPort updateIssuePlanPort;
    private final GetProductPort getProductPort;

    @Override
    @Transactional
    public IssuePlan update(IssuePlan issuePlan) {
        IssuePlan existingIssuePlan = updateIssuePlanPort.findById(issuePlan.getIssuePlanId())
                .orElseThrow(() -> new EntityNotFoundException("출고 예정 정보를 찾을 수 없습니다."));

        Product product = getProductPort.findByProductNumber(issuePlan.getProduct().getProductNumber())
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        existingIssuePlan.updateIssuePlan(
                product,
                issuePlan.getIssuePlanDate()
        );

        return updateIssuePlanPort.update(existingIssuePlan);

    }
}