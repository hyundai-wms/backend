package com.myme.mywarehome.domains.receipt.application.service;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.product.application.exception.ProductNotFoundException;
import com.myme.mywarehome.domains.product.application.port.out.GetProductPort;
import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.port.in.CreateReceiptPlanUseCase;
import com.myme.mywarehome.domains.receipt.application.port.in.command.CreateReceiptPlanCommand;
import com.myme.mywarehome.domains.receipt.application.port.out.CreateReceiptPlanPort;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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

        // 1. 기본 receiptPlan 생성
        ReceiptPlan receiptPlan = ReceiptPlan.builder()
                .receiptPlanDate(command.receiptPlanDate())
                .receiptPlanItemCount(command.itemCount())
                .build();

        // 2. P/N으로 Product 조회
        Product product = getProductPort.findByProductNumber(command.productNumber())
                .orElseThrow(ProductNotFoundException::new);

        // 3. Product 연결
        receiptPlan.connectWithProduct(product);

        // 4. ReceiptPlan 생성
        ReceiptPlan createdReceiptPlan = createReceiptPlanPort.create(receiptPlan);
        createdReceiptPlan.generateReceiptPlanCode();

        return createdReceiptPlan;
    }

    @Override
    @Transactional
    public List<ReceiptPlan> createReceiptPlanBulk(List<CreateReceiptPlanCommand> commandList) {
        // 1. 모든 product number 추출 및 한번에 조회
        Set<String> productNumbers = commandList.stream()
                .map(CreateReceiptPlanCommand::productNumber)
                .collect(Collectors.toSet());

        Map<String, Product> productMap = getProductPort.findAllByProductNumbers(productNumbers)
                .stream()
                .collect(Collectors.toMap(
                        Product::getProductNumber,
                        product -> product
                ));

        // 2. 각 command를 ReceiptPlan으로 변환 및 Product 연결
        List<ReceiptPlan> receiptPlans = commandList.stream()
                .map(command -> {
                    ReceiptPlan receiptPlan = ReceiptPlan.builder()
                            .receiptPlanDate(command.receiptPlanDate())
                            .receiptPlanItemCount(command.itemCount())
                            .build();

                    Product product = productMap.get(command.productNumber());
                    if (product == null) {
                        throw new ProductNotFoundException();
                    }

                    receiptPlan.connectWithProduct(product);
                    return receiptPlan;
                })
                .collect(Collectors.toList());

        // 3. Bulk 저장
        List<ReceiptPlan> createdReceiptPlanList = createReceiptPlanPort.createBulk(receiptPlans);

        // 4. 코드 생성
        createdReceiptPlanList.forEach(ReceiptPlan::generateReceiptPlanCode);

        return createdReceiptPlanList;
    }
}
