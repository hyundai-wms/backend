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
        Deque<MrpNodeDto> productDeque = new LinkedList<>();
        List<PurchaseOrderReport> purchaseReports = new ArrayList<>();
        List<ProductionPlanningReport> productionReports = new ArrayList<>();
        List<MrpExceptionReport> exceptionReports = new ArrayList<>();

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

            purchaseReports.addAll(result.purchaseOrderReports());
            productionReports.addAll(result.productionPlanningReports());

            // 자식 노드들 처리
            processChildNodes(currentNode, unifiedBomData, productDeque);
        }

        return new MrpCalculateResultDto(
                0,
                purchaseReports,
                productionReports,
                exceptionReports
        );
    }

    private void processChildNodes(MrpNodeDto parentNode, UnifiedBomDataDto unifiedBomData, Deque<MrpNodeDto> deque) {
        List<BomTree> children = unifiedBomData.bomTreeMap()
                .getOrDefault(parentNode.product().getProductId(), Collections.emptyList());

        for (BomTree child : children) {
            long requiredCount = parentNode.requiredPartsCount() * child.getChildCompositionRatio();
            MrpNodeDto childNode = new MrpNodeDto(
                    child.getChildProduct(),
                    requiredCount
            );

            // deque의 모든 원소를 순회하며 같은 품번이 있는지 확인
            boolean foundSameProduct = false;
            Iterator<MrpNodeDto> iterator = deque.iterator();
            int position = 0;
            int targetPosition = -1;

            while (iterator.hasNext()) {
                MrpNodeDto existingNode = iterator.next();
                if (hasSameProductNumber(existingNode, childNode)) {
                    foundSameProduct = true;
                    targetPosition = position;
                    break;
                }
                position++;
            }

            if (foundSameProduct) {
                // 같은 품번이 발견된 위치 다음에 노드를 삽입
                List<MrpNodeDto> temp = new ArrayList<>();
                for (int i = 0; i <= targetPosition; i++) {
                    temp.add(deque.pollFirst());
                }
                deque.addFirst(childNode);
                temp.forEach(deque::addFirst);

                log.debug("\n\n\n\n");
                Queue<MrpNodeDto> q = new LinkedList<>();
                while(!deque.isEmpty()) {
                    MrpNodeDto mnd = deque.pollFirst();
                    q.add(mnd);
                    log.debug(mnd.product().getProductNumber());
                }
                while(!q.isEmpty()) {
                    deque.add(q.poll());
                }
                log.debug("\n\n\n\n");

            } else {
                deque.addLast(childNode);
            }
        }
    }

    private boolean hasSameProductNumber(MrpNodeDto node1, MrpNodeDto node2) {
        return node1.product().getProductNumber().equals(
                node2.product().getProductNumber());
    }

}
