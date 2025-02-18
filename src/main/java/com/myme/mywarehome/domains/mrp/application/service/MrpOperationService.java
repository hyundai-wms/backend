package com.myme.mywarehome.domains.mrp.application.service;

import com.myme.mywarehome.domains.mrp.application.domain.BomTree;
import com.myme.mywarehome.domains.mrp.application.domain.EngineType;
import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecordItem;
import com.myme.mywarehome.domains.mrp.application.port.in.CreateInventoryRecordUseCase;
import com.myme.mywarehome.domains.mrp.application.port.in.MrpOperationUseCase;
import com.myme.mywarehome.domains.mrp.application.port.in.command.MrpInputCommand;
import com.myme.mywarehome.domains.mrp.application.port.out.GetBomTreePort;
import com.myme.mywarehome.domains.mrp.application.port.out.GetInventoryRecordPort;
import com.myme.mywarehome.domains.product.application.domain.Product;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MrpOperationService implements MrpOperationUseCase {
    private final CreateInventoryRecordUseCase createInventoryRecordUseCase;
    private final GetBomTreePort getBomTreePort;
    private final GetInventoryRecordPort getInventoryRecordPort;

    private Map<Long, InventoryRecordItem> inventoryRecord;
    private LocalDate computedDate;

    @Override
    @Transactional
    public void run(MrpInputCommand command) {
        // 1. 재고기록철 등록
        createInventoryRecordUseCase.createInventoryRecord();

        // 2. 재고기록철 조회
        List<InventoryRecordItem> inventoryRecordItemList = getInventoryRecordPort.findRecentInventoryRecord();
        inventoryRecord = inventoryRecordItemList.stream()
                .collect(Collectors.toMap(
                        item -> item.getProduct().getProductId(),  // key: ProductId
                        item -> item                               // value: InventoryRecordItem 자체
                ));

        // 3. BOM 조회
        String[] engineList = {"kappa", "gamma", "nu", "theta"};
        for(String engine: engineList){
            String dbEngineName = EngineType.convertToDbName(engine);
            List<BomTree> bomTree = getBomTreePort.findAllByApplicableEngine(dbEngineName);

            int requiredEngineCount = command.engineCountMap().get(engine);

            computedDate = command.dueDate();
            bomTreeTraversal(bomTree, requiredEngineCount);
        }
    }

    private void bomTreeTraversal(List<BomTree> bomTree, int requiredEngineCount) {
        // BOM 트리를 BFS로 탐색
        Queue<MrpNode> productQueue = new LinkedList<>();
        Set<Long> visited = new HashSet<>();

        // BomTree를 Map으로 변환
        Map<Long, List<BomTree>> bomTreeMap = bomTree.stream()
                .collect(Collectors.groupingBy(
                        tree -> tree.getParentProduct().getProductId()
                ));

        // 엔진을 일단 넣고 시작
        MrpNode initMrpNode = new MrpNode(
                bomTree.get(0).getParentProduct(),
                requiredEngineCount
        );
        productQueue.offer(initMrpNode);

        while (!productQueue.isEmpty()) {
            MrpNode currentNode = productQueue.poll();

            if (!visited.add(currentNode.product.getProductId())) {
                continue;
            }

            // Map을 사용하여 자식 제품들 찾기
            List<BomTree> children = bomTreeMap.getOrDefault(currentNode.product.getProductId(), Collections.emptyList());

            // currentProduct에 대한 처리(계산)
            int nextRequiredPartsCount = mrpCalculate(currentNode);

            // 자식 제품들을 큐에 추가
            for (BomTree child : children) {
                MrpNode childMrpNode = new MrpNode(
                        child.getChildProduct(),
                        nextRequiredPartsCount
                );
                productQueue.offer(childMrpNode);
            }
        }
    }

    private int mrpCalculate(MrpNode mrpNode) {
        // inventoryRecord : 재고기록철 맵
        // computedDate : 이번에 계산된 날짜
        // mrpNode : 어떤 물품과 그 때 필요한 수량

        // 1. 해당하는 트리의 노드를 바탕으로 계산

        // 2. 안전재고 계산
        //   - 안전재고는 이번에 들어온 수량 * 0.1
        //   - 이것도 저장해야 하네

        // 3. 발주/생산/예외 보고.. 생성

        // OUTPUT
        // 1. 발주 예정 보고서
        //   - 발주 일자(가상의 날짜)
        //   - 입고 되어 들어오는 날짜(입고 예정일)
        //   - 발주 물품
        //   - 발주 수량(EA인지 SET인지 계산 잘하기)
        // 2. 생산 지시 보고서
        //   - 생산 일자(입고 예정일)
        //   - 생산을 위해 하위 부품이 출고 되어야 하는 날짜(출고 예정일)
        //   - 생산 물품
        //   - 하위 부품 리스트
        //   - 생산 물품 수량
        // 3. 예외 보고서
        //   - 만약, 납기일 내로 생산하지 못한다면 에러
        //   - 만약, Bin 크기 이상으로 데이터가 입고되어야 하면 에러
        //   - 현재 재고가 안전재고 미만인 품목(WMS)
    }

    private record MrpNode(
            Product product,
            int requiredPartsCount
    ) {
    }
}
