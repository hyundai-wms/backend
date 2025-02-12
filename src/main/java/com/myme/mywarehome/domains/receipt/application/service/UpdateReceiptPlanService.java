package com.myme.mywarehome.domains.receipt.application.service;

import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.product.application.exception.ProductNotFoundException;
import com.myme.mywarehome.domains.product.application.port.out.GetProductPort;
import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.exception.ReceiptPlanNotFoundException;
import com.myme.mywarehome.domains.receipt.application.port.in.UpdateReceiptPlanUseCase;
import com.myme.mywarehome.domains.receipt.application.port.in.command.ReceiptPlanCommand;
import com.myme.mywarehome.domains.receipt.application.port.out.GetReceiptPlanPort;
import com.myme.mywarehome.domains.receipt.application.port.out.UpdateReceiptPlanPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateReceiptPlanService implements UpdateReceiptPlanUseCase {
    private final GetProductPort getProductPort;
    private final GetReceiptPlanPort getReceiptPlanPort;
    private final UpdateReceiptPlanPort updateReceiptPlanPort;

    @Override
    @Transactional
    public ReceiptPlan updateReceiptPlan(Long receiptPlanId, ReceiptPlanCommand command) {
        ReceiptPlan receiptPlan = getReceiptPlanPort.findReceiptPlanById(receiptPlanId)
                .orElseThrow(ReceiptPlanNotFoundException::new);

        // P/N 수정
        if (command.productNumber() != null) {
            Product newProduct = getProductPort.findByProductNumber(command.productNumber())
                    .orElseThrow(ProductNotFoundException::new);

            receiptPlan.connectWithProduct(newProduct);
        }

        // 입고할 재고 수량 수정
        if (command.itemCount() != null) {
            receiptPlan.changeReceiptPlanItemCount(command.itemCount());
        }

        // 입고예정일 수정
        if (command.receiptPlanDate() != null) {
            receiptPlan.changeReceiptPlanDate(command.receiptPlanDate());
        }

        return updateReceiptPlanPort.updateReceiptPlan(receiptPlan)
                .orElseThrow(ReceiptPlanNotFoundException::new);
    }
}
