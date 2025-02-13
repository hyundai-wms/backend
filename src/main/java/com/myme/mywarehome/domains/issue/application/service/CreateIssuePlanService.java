package com.myme.mywarehome.domains.issue.application.service;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.in.CreateIssuePlanUseCase;
import com.myme.mywarehome.domains.issue.application.port.in.command.IssuePlanCommand;
import com.myme.mywarehome.domains.issue.application.port.out.CreateIssuePlanPort;
import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.product.application.exception.ProductNotFoundException;
import com.myme.mywarehome.domains.product.application.port.out.GetProductPort;
import com.myme.mywarehome.infrastructure.util.helper.StringHelper;
import jakarta.transaction.Transactional;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateIssuePlanService implements CreateIssuePlanUseCase {
    private final CreateIssuePlanPort createIssuePlanPort;
    private final GetProductPort getProductPort;

    @Override
    @Transactional
    public IssuePlan create(IssuePlanCommand command) {
        // productnumber로 가져오기
        Product product = getProductPort.findByProductNumber(command.productNumber()).orElseThrow(
                ProductNotFoundException::new);

        // IssuePlan 객체 만들기
        IssuePlan issuePlan = IssuePlan.builder()
                .issuePlanItemCount(command.itemCount())
                .issuePlanDate(command.issuePlanDate())
                .product(product)
                .build();

        // 만든 IssuePlan 사용해서 savedIssuePlan
        IssuePlan savedIssuePlan = createIssuePlanPort.create(issuePlan);
        // ID를 이용해서 코드 생성
        String code = StringHelper.CodeGenerator.generateIssuePlanCode(savedIssuePlan.getIssuePlanId());

        savedIssuePlan.setIssuePlanCode(code);

        return savedIssuePlan;
    }

    @Override
    @Transactional
    public List<IssuePlan> createBulk(List<IssuePlanCommand> commandList) {
        // 1. 모든 product number 추출 및 한번에 조회
        Set<String> productNumbers = commandList.stream()
                .map(IssuePlanCommand::productNumber)
                .collect(Collectors.toSet());

        Map<String, Product> productMap = getProductPort.findAllByProductNumbers(productNumbers)
                .stream()
                .collect(Collectors.toMap(
                        Product::getProductNumber,
                        product -> product
                ));

        // 2. 각 command를 ReceiptPlan으로 변환 및 Product 연결
        List<IssuePlan> issuePlans = commandList.stream()
                .map(command -> {
                    Product product = productMap.get(command.productNumber());
                    if (product == null) {
                        throw new ProductNotFoundException();
                    }

                    return IssuePlan.builder()
                            .issuePlanDate(command.issuePlanDate())
                            .issuePlanItemCount(command.itemCount())
                            .product(product)
                            .build();
                })
                .toList();

        // 3. Bulk 저장
        List<IssuePlan> createdIssuePlanList = createIssuePlanPort.createBulk(issuePlans);

        // 4. 코드 생성
        createdIssuePlanList.forEach((issuePlan) -> {
            String code = StringHelper.CodeGenerator.generateIssuePlanCode(issuePlan.getIssuePlanId());
            issuePlan.setIssuePlanCode(code);
        }) ;


        return createdIssuePlanList;
    }

}
