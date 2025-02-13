package com.myme.mywarehome.domains.receipt.application.service;

import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.product.application.exception.ProductNotFoundException;
import com.myme.mywarehome.domains.product.application.port.out.GetProductPort;
import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.port.in.CreateReceiptPlanUseCase;
import com.myme.mywarehome.domains.receipt.application.port.in.command.ReceiptPlanCommand;
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
    public ReceiptPlan createReceiptPlan(ReceiptPlanCommand command) {
        // 1. P/N으로 Product 조회
        Product product = getProductPort.findByProductNumber(command.productNumber())
                .orElseThrow(ProductNotFoundException::new);

        // 2. ReceiptPlan 생성 및 연관관계 설정
        ReceiptPlan receiptPlan = ReceiptPlan.builder()
                .receiptPlanDate(command.receiptPlanDate())
                .receiptPlanItemCount(command.itemCount())
                .product(product)  // 생성자에서 바로 주입
                .build();

        // 3. 저장 (이 시점에 @PostPersist로 receiptPlanCode가 생성됨)
        return createReceiptPlanPort.create(receiptPlan);
    }

    @Override
    @Transactional
    public List<ReceiptPlan> createReceiptPlanBulk(List<ReceiptPlanCommand> commandList) {
        // 1. 모든 product number 추출 및 한번에 조회
        Set<String> productNumbers = commandList.stream()
                .map(ReceiptPlanCommand::productNumber)
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
                    Product product = productMap.get(command.productNumber());
                    if (product == null) {
                        throw new ProductNotFoundException();
                    }

                    return ReceiptPlan.builder()
                            .receiptPlanDate(command.receiptPlanDate())
                            .receiptPlanItemCount(command.itemCount())
                            .product(product)  // 생성자에서 바로 주입
                            .build();
                })
                .toList();

        // 3. Bulk 저장
        return createReceiptPlanPort.createBulk(receiptPlans);
    }
}
