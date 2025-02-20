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

import java.time.LocalDate;
import java.util.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MrpBomTreeTraversalService implements MrpBomTreeTraversalUseCase {
    private final MrpCalculatorUseCase mrpCalculatorUseCase;

    @Override
    public MrpCalculateResultDto traverse(UnifiedBomDataDto unifiedBomData, MrpContextDto context) {
        LinkedList<MrpNodeDto> productDeque = new LinkedList<>();
        List<PurchaseOrderReport> purchaseReports = new ArrayList<>();
        List<ProductionPlanningReport> productionReports = new ArrayList<>();
        List<MrpExceptionReport> exceptionReports = new ArrayList<>();

        // vendor 노드들의 계산 결과를 모으기 위한 리스트
        List<MrpCalculateResultDto> vendorResults = new ArrayList<>();

        // 루트 노드로 시작
        MrpNodeDto rootNode = new MrpNodeDto(
                unifiedBomData.virtualRoot(),
                1  // 가상 루트의 수량은 1
        );
        productDeque.offerLast(rootNode);

        while (!productDeque.isEmpty()) {
            MrpNodeDto currentNode = productDeque.pollFirst();

            // 가상 루트는 건너뛰기
            if (currentNode.product().getProductNumber().equals("VIRTUAL-ROOT")) {
                // 자식 노드들만 큐에 추가
                processChildNodes(currentNode, unifiedBomData, productDeque);
                continue;
            }

            // 실제 제품 노드 처리
            MrpCalculateResultDto result = mrpCalculatorUseCase.calculate(currentNode, context);
            if (result.hasExceptions()) {
                return result;
            }

            // vendor인 경우 따로 보관
            if (currentNode.product().getCompany().getIsVendor()) {
                vendorResults.add(result);
            } else {
                // vendor가 아닌 경우 바로 추가
                purchaseReports.addAll(result.purchaseOrderReports());
                productionReports.addAll(result.productionPlanningReports());
            }

            // 자식 노드들 처리
            processChildNodes(currentNode, unifiedBomData, productDeque);
        }

        // vendor 결과들 처리
        if (!vendorResults.isEmpty()) {
            // 최대 리드타임 찾기
            int maxLeadTime = vendorResults.stream()
                    .mapToInt(MrpCalculateResultDto::leadTimeDays)
                    .max()
                    .orElse(0);

            // 모든 vendor 발주를 최대 리드타임 기준으로 조정
            for (MrpCalculateResultDto vendorResult : vendorResults) {
                for (PurchaseOrderReport report : vendorResult.purchaseOrderReports()) {
                    LocalDate purchaseOrderDate = context.getComputedDate().minusDays(maxLeadTime);
                    LocalDate receiptPlanDate = purchaseOrderDate.plusDays(vendorResult.leadTimeDays());

                    PurchaseOrderReport adjustedReport = PurchaseOrderReport.builder()
                            .purchaseOrderDate(purchaseOrderDate)
                            .receiptPlanDate(receiptPlanDate)
                            .product(report.getProduct())
                            .quantity(report.getQuantity())
                            .safeItemCount(report.getSafeItemCount())
                            .build();
                    purchaseReports.add(adjustedReport);
                }
            }
        }

        return new MrpCalculateResultDto(
                0,
                purchaseReports,
                productionReports,
                exceptionReports,
                0
        );
    }

    private void processChildNodes(MrpNodeDto parentNode, UnifiedBomDataDto unifiedBomData, LinkedList<MrpNodeDto> deque) {
        List<BomTree> children = unifiedBomData.bomTreeMap()
                .getOrDefault(parentNode.product().getProductId(), Collections.emptyList());

        for (BomTree child : children) {
            long requiredCount = parentNode.requiredPartsCount() * child.getChildCompositionRatio();
            MrpNodeDto childNode = new MrpNodeDto(
                    child.getChildProduct(),
                    requiredCount
            );

            // 같은 품번이 있는지 검사 (같은 레벨 내에서)
            int duplicateIndex = -1;
            for (int i = 0; i < deque.size(); i++) {
                MrpNodeDto node = deque.get(i);
                if (node.product().getProductNumber().equals(childNode.product().getProductNumber())) {
                    duplicateIndex = i;
                    break;
                }
            }

            if (duplicateIndex != -1) {
                // 중복된 품번이 있으면, 해당 노드 바로 뒤에 삽입
                deque.add(duplicateIndex + 1, childNode);
            } else {
                // 없으면 맨 뒤에 추가
                deque.addLast(childNode);
            }
        }
    }

}
