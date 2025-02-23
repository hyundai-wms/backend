package com.myme.mywarehome.domains.mrp.application.service;

import com.myme.mywarehome.domains.mrp.application.domain.BomTree;
import com.myme.mywarehome.domains.mrp.application.domain.MrpExceptionReport;
import com.myme.mywarehome.domains.mrp.application.domain.ProductionPlanningReport;
import com.myme.mywarehome.domains.mrp.application.domain.PurchaseOrderReport;
import com.myme.mywarehome.domains.mrp.application.port.in.MrpBomTreeTraversalUseCase;
import com.myme.mywarehome.domains.mrp.application.port.in.MrpCalculatorUseCase;
import com.myme.mywarehome.domains.mrp.application.port.in.command.MrpInputCommand;
import com.myme.mywarehome.domains.mrp.application.service.dto.*;
import com.myme.mywarehome.infrastructure.config.aspect.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MrpBomTreeTraversalService implements MrpBomTreeTraversalUseCase {

    private final MrpCalculatorUseCase mrpCalculatorUseCase;

    @Override
    @LogExecutionTime(value = "MRP BomTree Traversal")
    public MrpCalculateResultDto traverse(MrpInputCommand command,
            UnifiedBomDataDto unifiedBomData,
            MrpContextDto context) {
        // 전체 트래버설 시간 측정
        long startTime = System.currentTimeMillis();
        String operationId = MDC.get("operationId");

        // 트래버설 시작 로그
        log.info("MRP BOM Tree traversal started - operationId: {}, virtualRoot: {}, computedDate: {}",
                operationId,
                unifiedBomData.virtualRoot().getProductNumber(),
                context.getComputedDate());

        LinkedList<MrpNodeDto> productDeque = new LinkedList<>();
        List<PurchaseOrderReport> purchaseReports = new ArrayList<>();
        List<ProductionPlanningReport> productionReports = new ArrayList<>();
        List<MrpExceptionReport> exceptionReports = new ArrayList<>();

        // vendor 노드들의 계산 결과를 모으기 위한 리스트
        List<MrpCalculateResultDto> vendorResults = new ArrayList<>();

        // 문제가 발생한 노드 저장
        List<MrpProblemNode> problemNodes = new ArrayList<>();

        // 루트 노드로 시작
        MrpNodeDto rootNode = new MrpNodeDto(
                unifiedBomData.virtualRoot(),
                1  // 가상 루트의 수량은 1
        );
        productDeque.offerLast(rootNode);

        while (!productDeque.isEmpty()) {
            MrpNodeDto currentNode = productDeque.pollFirst();

            // 각 노드(제품) 처리 전 로그
            log.info("Processing node - productNumber: {}, requiredPartsCount: {}",
                    currentNode.product().getProductNumber(),
                    currentNode.requiredPartsCount());

            // 가상 루트는 건너뛰기
            if ("VIRTUAL-ROOT".equals(currentNode.product().getProductNumber())) {
                processChildNodes(currentNode, unifiedBomData, productDeque);
                continue;
            }

            // 실제 제품 노드 처리
            MrpCalculateResultDto result = mrpCalculatorUseCase.calculate(currentNode, context);

            // 계산 결과 로그
            log.info("Calculation result - productNumber: {}, orderQuantity: {}, exceptionCount: {}, leadTimeDays: {}",
                    currentNode.product().getProductNumber(),
                    result.nextRequiredPartsCount(), // 수정된 부분
                    result.mrpExceptionReports().size(),
                    result.leadTimeDays());

            // 예외가 있으면 problemNodes에 추가
            if (result.hasExceptions()) {
                MrpExceptionReport firstException = result.mrpExceptionReports().get(0);
                problemNodes.add(new MrpProblemNode(
                        currentNode.product(),
                        firstException.getExceptionType(),
                        firstException.getExceptionMessage(),
                        currentNode.requiredPartsCount(),
                        result.leadTimeDays(),
                        currentNode.product().getBayList().size() * 10
                ));

                log.warn("Exception occurred - productNumber: {}, exceptionType: {}, exceptionMessage: {}",
                        currentNode.product().getProductNumber(),
                        firstException.getExceptionType(),
                        firstException.getExceptionMessage());

                if (currentNode.product().getCompany().getIsVendor()) {
                    vendorResults.add(result);
                }
                // 예외 발생 시 계속 자식 노드를 탐색할지 여부는 비즈니스 로직에 따라 결정
                continue;
            }

            // vendor인 경우 따로 보관
            if (currentNode.product().getCompany().getIsVendor()) {
                vendorResults.add(result);
            } else {
                // vendor가 아닌 경우 보고서들 바로 추가
                purchaseReports.addAll(result.purchaseOrderReports());
                productionReports.addAll(result.productionPlanningReports());
            }

            // 자식 노드들 처리
            processChildNodes(currentNode, unifiedBomData, productDeque);
        }

        // 문제가 있는 경우 최종 예외 처리
        if (!problemNodes.isEmpty()) {
            log.warn("Problem nodes detected - count: {}", problemNodes.size());

            // vendor의 최대 LT 찾기
            int maxLeadTime = vendorResults.stream()
                    .mapToInt(MrpCalculateResultDto::leadTimeDays)
                    .max()
                    .orElse(0);

            LocalDate expectedMinOrderDate = context.getComputedDate().minusDays(maxLeadTime);
            long daysBetween = Math.abs(ChronoUnit.DAYS.between(expectedMinOrderDate, LocalDate.now()));
            LocalDate minDueDate = command.dueDate().plusDays(daysBetween);

            List<MrpExceptionReport> finalExceptionReports = problemNodes.stream()
                    .map(node -> {
                        String solution = node.exceptionType().equals("LEAD_TIME_VIOLATION")
                                ? String.format("이 제품의 최소 납기일은 %s입니다.", minDueDate)
                                : String.format("현재 가용 Bin으로 처리 가능한 최대 수량은 %d입니다.",
                                        node.availableBins() * 10 * 25);

                        return MrpExceptionReport.builder()
                                .exceptionType(node.exceptionType())
                                .exceptionMessage(node.exceptionMessage())
                                .solution(solution)
                                .build();
                    })
                    .toList();

            long endTime = System.currentTimeMillis();
            long totalDuration = endTime - startTime;
            log.warn("MRP BOM Tree traversal ended with exceptions - Duration: {}ms, operationId: {}, problemCount: {}",
                    totalDuration, operationId, problemNodes.size());

            return new MrpCalculateResultDto(
                    -1,
                    new ArrayList<>(),
                    new ArrayList<>(),
                    finalExceptionReports,
                    maxLeadTime
            );
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
            log.info("Vendor results adjusted - maxLeadTime: {}, vendorResultCount: {}",
                    maxLeadTime, vendorResults.size());
        }

        // 최종 결과 리턴
        long endTime = System.currentTimeMillis();
        long totalDuration = endTime - startTime;
        log.info("MRP BOM Tree traversal completed successfully - Duration: {}ms, operationId: {}, purchaseCount: {}, productionCount: {}",
                totalDuration,
                operationId,
                purchaseReports.size(),
                productionReports.size());

        return new MrpCalculateResultDto(
                0,
                purchaseReports,
                productionReports,
                exceptionReports,
                0
        );
    }

    private void processChildNodes(MrpNodeDto parentNode,
            UnifiedBomDataDto unifiedBomData,
            LinkedList<MrpNodeDto> deque) {
        List<BomTree> children = unifiedBomData.bomTreeMap()
                .getOrDefault(parentNode.product().getProductId(), Collections.emptyList());

        for (BomTree child : children) {
            long requiredCount = parentNode.requiredPartsCount() * child.getChildCompositionRatio();
            MrpNodeDto childNode = new MrpNodeDto(child.getChildProduct(), requiredCount);

            // 로그 예시: 자식 노드 생성
            log.debug("Child node - parentProduct: {}, childProduct: {}, childRequiredCount: {}",
                    parentNode.product().getProductNumber(),
                    childNode.product().getProductNumber(),
                    childNode.requiredPartsCount());

            // 같은 품번이 있는지 검사
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
