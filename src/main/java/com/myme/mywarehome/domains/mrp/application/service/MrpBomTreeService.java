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

        // 4. 레벨별로 중복 노드 처리
        List<BomTree> mergedBomTrees = mergeDuplicateNodes(allBomTrees);

        // 5. BomTree Map 생성 (parent product id -> List<BomTree>)
        Map<Long, List<BomTree>> bomTreeMap = mergedBomTrees.stream()
                .collect(Collectors.groupingBy(
                        tree -> tree.getParentProduct().getProductId()
                ));

        return new UnifiedBomDataDto(virtualRoot, mergedBomTrees, bomTreeMap);
    }

    private List<BomTree> mergeDuplicateNodes(List<BomTree> originalBomTrees) {
        // Level별 노드 맵핑 (Product Number -> List<BomTree>)
        Map<Integer, Map<String, List<BomTree>>> levelProductMap = new HashMap<>();

        // 1. 각 BomTree의 레벨 계산 및 맵핑
        Map<String, Integer> productLevels = calculateProductLevels(originalBomTrees);

        // 2. 레벨별로 동일 제품 그룹화
        for (BomTree tree : originalBomTrees) {
            String productNumber = tree.getChildProduct().getProductNumber();
            int level = productLevels.get(productNumber);

            levelProductMap.computeIfAbsent(level, k -> new HashMap<>())
                    .computeIfAbsent(productNumber, k -> new ArrayList<>())
                    .add(tree);
        }

        // 3. 중복 노드 병합
        List<BomTree> mergedTrees = new ArrayList<>();
        Set<String> processedProducts = new HashSet<>();

        for (Map.Entry<Integer, Map<String, List<BomTree>>> levelEntry : levelProductMap.entrySet()) {
            for (Map.Entry<String, List<BomTree>> productEntry : levelEntry.getValue().entrySet()) {
                List<BomTree> duplicates = productEntry.getValue();
                if (duplicates.size() > 1) {
                    // 중복된 노드가 있는 경우
                    BomTree firstTree = duplicates.get(0);
                    int totalRatio = duplicates.stream()
                            .mapToInt(BomTree::getChildCompositionRatio)
                            .sum();

                    // 첫 번째 tree의 구성비만 수정하여 사용
                    BomTree mergedTree = BomTree.builder()
                            .parentProduct(firstTree.getParentProduct())
                            .childProduct(firstTree.getChildProduct())
                            .childCompositionRatio(totalRatio)
                            .build();

                    mergedTrees.add(mergedTree);
                } else {
                    // 중복이 없는 경우 그대로 사용
                    mergedTrees.add(duplicates.get(0));
                }
            }
        }

        return mergedTrees;
    }

    private Map<String, Integer> calculateProductLevels(List<BomTree> bomTrees) {
        Map<String, Integer> levels = new HashMap<>();
        Map<String, Set<String>> parentToChildren = new HashMap<>();
        Set<String> rootProducts = new HashSet<>();

        // 부모-자식 관계 맵 구성
        for (BomTree tree : bomTrees) {
            String parentNumber = tree.getParentProduct().getProductNumber();
            String childNumber = tree.getChildProduct().getProductNumber();

            parentToChildren.computeIfAbsent(parentNumber, k -> new HashSet<>()).add(childNumber);
            rootProducts.add(parentNumber);
        }

        // 루트 노드 찾기 (다른 노드의 자식이 아닌 노드)
        for (BomTree tree : bomTrees) {
            String childNumber = tree.getChildProduct().getProductNumber();
            rootProducts.removeIf(root -> parentToChildren.values().stream()
                    .anyMatch(children -> children.contains(root)));
        }

        // BFS로 레벨 계산
        Queue<String> queue = new LinkedList<>(rootProducts);
        rootProducts.forEach(root -> levels.put(root, 0));

        while (!queue.isEmpty()) {
            String current = queue.poll();
            int currentLevel = levels.get(current);

            Set<String> children = parentToChildren.getOrDefault(current, Collections.emptySet());
            for (String child : children) {
                if (!levels.containsKey(child)) {
                    levels.put(child, currentLevel + 1);
                    queue.offer(child);
                }
            }
        }

        return levels;
    }
}
