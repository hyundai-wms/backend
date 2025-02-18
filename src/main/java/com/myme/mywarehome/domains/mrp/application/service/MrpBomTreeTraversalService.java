package com.myme.mywarehome.domains.mrp.application.service;

import com.myme.mywarehome.domains.mrp.application.domain.BomTree;
import com.myme.mywarehome.domains.mrp.application.domain.MrpExceptionReport;
import com.myme.mywarehome.domains.mrp.application.domain.ProductionPlanningReport;
import com.myme.mywarehome.domains.mrp.application.domain.PurchaseOrderReport;
import com.myme.mywarehome.domains.mrp.application.port.in.MrpBomTreeTraversalUseCase;
import com.myme.mywarehome.domains.mrp.application.port.in.MrpCalculatorUseCase;
import com.myme.mywarehome.domains.mrp.application.service.dto.MrpCalculateResultDto;
import com.myme.mywarehome.domains.mrp.application.service.dto.MrpContextDto;
import com.myme.mywarehome.domains.mrp.application.service.dto.MrpNodeDto;
import com.myme.mywarehome.domains.mrp.application.service.dto.UnifiedBomDataDto;
import com.myme.mywarehome.domains.product.application.domain.Product;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MrpBomTreeTraversalService implements MrpBomTreeTraversalUseCase {
    private final MrpCalculatorUseCase mrpCalculatorUseCase;

    @Override
    public MrpCalculateResultDto traverse(UnifiedBomDataDto unifiedBomData, MrpContextDto context) {
        Queue<MrpNodeDto> productQueue = new LinkedList<>();
        Set<Long> visited = new HashSet<>();
        List<PurchaseOrderReport> purchaseReports = new ArrayList<>();
        List<ProductionPlanningReport> productionReports = new ArrayList<>();
        List<MrpExceptionReport> exceptionReports = new ArrayList<>();

        // 루트 노드로 시작
        MrpNodeDto rootNode = new MrpNodeDto(
                unifiedBomData.virtualRoot(),
                1  // 가상 루트의 수량은 1
        );
        productQueue.offer(rootNode);

        while (!productQueue.isEmpty()) {
            MrpNodeDto currentNode = productQueue.poll();
            Long currentProductId = currentNode.product().getProductId();

            // 이미 방문한 노드는 건너뛰되, 공통 부품이면 수량을 누적
            if (visited.contains(currentProductId)) {
                if (isCommonPart(currentNode.product())) {
                    // 공통 부품의 경우 이전 계산 결과에 현재 수량을 더해서 재계산
                    MrpCalculateResultDto additionalResult = mrpCalculatorUseCase.calculate(currentNode, context);
                    if (additionalResult.hasExceptions()) {
                        return additionalResult;
                    }
                    purchaseReports.addAll(additionalResult.purchaseOrderReports());
                    productionReports.addAll(additionalResult.productionPlanningReports());
                }
                continue;
            }

            // 현재 노드가 가상 루트가 아닌 경우에만 계산 수행
            if (!currentNode.product().getProductNumber().equals("VIRTUAL-ROOT")) {
                MrpCalculateResultDto result = mrpCalculatorUseCase.calculate(currentNode, context);
                if (result.hasExceptions()) {
                    return result;
                }
                purchaseReports.addAll(result.purchaseOrderReports());
                productionReports.addAll(result.productionPlanningReports());
            }

            visited.add(currentProductId);

            // 자식 노드들 큐에 추가
            List<BomTree> children = unifiedBomData.bomTreeMap()
                    .getOrDefault(currentProductId, Collections.emptyList());

            for (BomTree child : children) {
                long requiredCount = currentNode.requiredPartsCount() * child.getChildCompositionRatio();
                MrpNodeDto childNode = new MrpNodeDto(
                        child.getChildProduct(),
                        requiredCount
                );
                productQueue.offer(childNode);
            }
        }

        return new MrpCalculateResultDto(
                0,
                purchaseReports,
                productionReports,
                exceptionReports
        );
    }

    private boolean isCommonPart(Product product) {
        String productNumber = product.getProductNumber();
        return productNumber.endsWith("01P00") || productNumber.endsWith("02P00");
    }

}
