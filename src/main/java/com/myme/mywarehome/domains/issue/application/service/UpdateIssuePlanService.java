package com.myme.mywarehome.domains.issue.application.service;

import com.myme.mywarehome.domains.issue.adapter.out.exception.IssuePlanExceedStockException;
import com.myme.mywarehome.domains.issue.adapter.out.exception.IssuePlanNotFoundException;
import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.in.UpdateIssuePlanUseCase;
import com.myme.mywarehome.domains.issue.application.port.in.command.IssuePlanCommand;
import com.myme.mywarehome.domains.issue.application.port.out.GetIssuePlanPort;
import com.myme.mywarehome.domains.issue.application.port.out.UpdateIssuePlanPort;
import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.product.application.exception.ProductNotFoundException;
import com.myme.mywarehome.domains.product.application.port.out.GetProductPort;
import com.myme.mywarehome.domains.receipt.application.port.out.GetReceiptPlanPort;
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
    private final GetIssuePlanPort getIssuePlanPort;

    @Override
    @Transactional
    public IssuePlan update(Long issuePlanId, IssuePlanCommand issuePlanCommand) {
        IssuePlan issuePlan = getIssuePlanPort.getIssuePlanById(issuePlanId).orElseThrow(EntityNotFoundException::new);

        // P/N 수정
        if(issuePlanCommand.productNumber() != null) {
            Product newProduct = getProductPort.findByProductNumber(issuePlanCommand.productNumber())
                    .orElseThrow(ProductNotFoundException::new);

            issuePlan.connectWithProduct(newProduct);
        }

        // 재고 수정
        if (issuePlanCommand.itemCount() != null) {
            issuePlan.changeIssuePlanItemCount(issuePlanCommand.itemCount());
        }

        if (issuePlanCommand.issuePlanDate() != null) {
            issuePlan.changeIssuePlanDate(issuePlanCommand.issuePlanDate());
        }

        return updateIssuePlanPort.update(issuePlan).orElseThrow(IssuePlanNotFoundException::new);

    }
}