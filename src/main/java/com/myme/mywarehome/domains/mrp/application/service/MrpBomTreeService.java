package com.myme.mywarehome.domains.mrp.application.service;

import com.myme.mywarehome.domains.mrp.application.domain.BomTree;
import com.myme.mywarehome.domains.mrp.application.domain.EngineType;
import com.myme.mywarehome.domains.mrp.application.port.in.MrpBomTreeUseCase;
import com.myme.mywarehome.domains.mrp.application.port.in.command.MrpInputCommand;
import com.myme.mywarehome.domains.mrp.application.port.out.GetBomTreePort;
import com.myme.mywarehome.domains.mrp.application.service.dto.UnifiedBomDataDto;
import com.myme.mywarehome.domains.product.application.domain.Product;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
public class MrpBomTreeService implements MrpBomTreeUseCase {
    private final GetBomTreePort getBomTreePort;

    @Override
    public UnifiedBomDataDto createUnifiedBomTree(MrpInputCommand command) {
        // 1. 각 엔진별 BOM Tree 조회 및 통합
        Map<String, String> engineNameMap = Map.of(
                "kappa", "03",
                "gamma", "04",
                "nu", "05",
                "theta", "06"
        );

        List<BomTree> allBomTrees = new ArrayList<>();
        Map<String, Product> engineProducts = new HashMap<>();

        // 각 엔진의 BOM Tree 조회
        for (Map.Entry<String, String> entry : engineNameMap.entrySet()) {
            String engineType = entry.getKey();
            String dbEngineName = EngineType.convertToDbName(engineType);
            List<BomTree> engineBomTree = getBomTreePort.findAllByApplicableEngine(dbEngineName);

            if (!engineBomTree.isEmpty()) {
                allBomTrees.addAll(engineBomTree);
                engineProducts.put(engineType, engineBomTree.get(0).getParentProduct());
            }
        }

        // 2. 가상의 루트 제품 생성
        Product virtualRoot = Product.builder()
                .productId(-1L)
                .productNumber("VIRTUAL-ROOT")
                .productName("Virtual Root")
                .eachCount(1)
                .leadTime(0)
                .build();

        // 3. 가상 루트와 엔진들을 연결하는 BomTree 생성
        for (Map.Entry<String, Integer> entry : command.engineCountMap().entrySet()) {
            String engineType = entry.getKey();
            Integer requiredCount = entry.getValue();
            Product engineProduct = engineProducts.get(engineType);

            BomTree rootConnection = BomTree.builder()
                    .parentProduct(virtualRoot)
                    .childProduct(engineProduct)
                    .childCompositionRatio(requiredCount)
                    .build();

            allBomTrees.add(rootConnection);
        }

        // 4. BomTree Map 생성 (parent product id -> List<BomTree>)
        Map<Long, List<BomTree>> bomTreeMap = allBomTrees.stream()
                .collect(Collectors.groupingBy(
                        tree -> tree.getParentProduct().getProductId()
                ));

        return new UnifiedBomDataDto(virtualRoot, allBomTrees, bomTreeMap);
    }

}
