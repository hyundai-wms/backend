package com.myme.mywarehome.domains.receipt.application.service;

import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.product.application.exception.ProductNotFoundException;
import com.myme.mywarehome.domains.product.application.port.out.GetProductPort;
import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.port.in.CreateReceiptPlanUseCase;
import com.myme.mywarehome.domains.receipt.application.port.in.command.CreateReceiptPlanCommand;
import com.myme.mywarehome.domains.receipt.application.port.out.CreateReceiptPlanPort;
import com.myme.mywarehome.domains.receipt.application.port.out.GetReceiptPlanPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateReceiptPlanService implements CreateReceiptPlanUseCase {
    private final CreateReceiptPlanPort createReceiptPlanPort;
    private final GetProductPort getProductPort;

    @Override
    @Transactional
    public ReceiptPlan createReceiptPlan(CreateReceiptPlanCommand command) {

        // 기본 receiptPlan 생성
        ReceiptPlan receiptPlan = ReceiptPlan.builder()
                .receiptPlanDate(command.receiptPlanDate())
                .receiptPlanItemCount(command.itemCount())
                .build();

        // P/N으로 Product 조회
        Product product = getProductPort.findByProductNumber(command.productNumber())
                .orElseThrow(ProductNotFoundException::new);

        // Product 연결
        receiptPlan.connectWithProduct(product);

        // ReceiptPlan 생성
        ReceiptPlan createdReceiptPlan = createReceiptPlanPort.create(receiptPlan);
        createdReceiptPlan.generateReceiptPlanCode();

        return createdReceiptPlan;
    }
}
