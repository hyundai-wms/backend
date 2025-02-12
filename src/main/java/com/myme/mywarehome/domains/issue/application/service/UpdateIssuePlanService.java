package com.myme.mywarehome.domains.issue.application.service;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.in.UpdateIssuePlanUseCase;
import com.myme.mywarehome.domains.issue.application.port.out.UpdateIssuePlanPort;
import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.product.application.exception.ProductNotFoundException;
import com.myme.mywarehome.domains.product.application.port.out.GetProductPort;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
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
        // 1. 기존 출고 예정 정보 조회
        IssuePlan existingIssuePlan = updateIssuePlanPort.findById(issuePlan.getIssuePlanId())
                .orElseThrow(() -> new EntityNotFoundException("출고 예정 정보를 찾을 수 없습니다."));

        // 2. 상품 정보 조회 및 검증
        Product product = null;
        if (issuePlan.getProduct() != null && issuePlan.getProduct().getProductNumber() != null) {
            product = getProductPort.findByProductNumber(issuePlan.getProduct().getProductNumber())
                    .orElseThrow(ProductNotFoundException::new);

            // 2-1. 상품 재고 검증
            if (issuePlan.getIssuePlanItemCount() != null &&
                    product.getEachCount() < issuePlan.getIssuePlanItemCount()) {
                throw new IllegalStateException("출고 예정 수량이 현재 재고량보다 많습니다.");
            }
        }


        existingIssuePlan.updateIssuePlan(
                product,
                issuePlan.getIssuePlanDate(),
                issuePlan.getIssuePlanItemCount()
        );

        return updateIssuePlanPort.update(existingIssuePlan);

    }
}