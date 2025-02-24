package com.myme.mywarehome.domains.mrp.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.myme.mywarehome.domains.company.application.domain.Company;
import com.myme.mywarehome.domains.mrp.application.domain.*;
import com.myme.mywarehome.domains.mrp.application.port.in.MrpCalculatorUseCase;
import com.myme.mywarehome.domains.mrp.application.port.in.command.MrpInputCommand;
import com.myme.mywarehome.domains.mrp.application.service.dto.MrpCalculateResultDto;
import com.myme.mywarehome.domains.mrp.application.service.dto.MrpContextDto;
import com.myme.mywarehome.domains.mrp.application.service.dto.MrpNodeDto;
import com.myme.mywarehome.domains.mrp.application.service.dto.UnifiedBomDataDto;
import com.myme.mywarehome.domains.product.application.domain.Product;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MrpBomTreeTraversalServiceTest {

    @Mock
    private MrpCalculatorUseCase mrpCalculatorUseCase;

    private MrpBomTreeTraversalService mrpBomTreeTraversalService;
    private LocalDate dueDate;
    private Product virtualRoot;
    private Product parentProduct;
    private Product childProduct;
    private Company normalCompany;
    private Company vendorCompany;
    private Map<Long, InventoryRecordItem> inventoryRecordMap;
    private InventoryRecord inventoryRecord;

    @BeforeEach
    void setUp() {
        mrpBomTreeTraversalService = new MrpBomTreeTraversalService(mrpCalculatorUseCase);
        dueDate = LocalDate.now().plusDays(30);

        normalCompany = Company.builder()
                .companyId(1L)
                .isVendor(false)
                .build();

        vendorCompany = Company.builder()
                .companyId(2L)
                .isVendor(true)
                .build();

        virtualRoot = Product.builder()
                .productId(-1L)
                .productNumber("VIRTUAL-ROOT")
                .productName("Virtual Root")
                .company(normalCompany)
                .build();

        parentProduct = Product.builder()
                .productId(1L)
                .productNumber("PARENT-001")
                .productName("Parent Product")
                .company(normalCompany)
                .build();

        childProduct = Product.builder()
                .productId(2L)
                .productNumber("CHILD-001")
                .productName("Child Product")
                .company(vendorCompany)
                .build();

        // 재고 기록 설정
        inventoryRecord = InventoryRecord.builder()
                .stockStatusAt(LocalDateTime.now())
                .build();

        inventoryRecordMap = new HashMap<>();
        inventoryRecordMap.put(parentProduct.getProductId(),
                InventoryRecordItem.builder()
                        .inventoryRecord(inventoryRecord)
                        .product(parentProduct)
                        .stockCount(100L)
                        .compositionRatio(1)
                        .leadTime(7)
                        .build()
        );

        inventoryRecordMap.put(childProduct.getProductId(),
                InventoryRecordItem.builder()
                        .inventoryRecord(inventoryRecord)
                        .product(childProduct)
                        .stockCount(50L)
                        .compositionRatio(1)
                        .leadTime(5)
                        .build()
        );
    }

    @Test
    @DisplayName("정상적인 BOM 트리를 순회하여 MRP 계산 결과를 반환한다")
    void traverse_withValidBomTree_shouldReturnCalculationResults() {
        // given
        Map<String, Integer> engineCountMap = Map.of("kappa", 1);
        MrpInputCommand command = new MrpInputCommand(engineCountMap, dueDate);

        List<BomTree> bomTrees = List.of(
                BomTree.builder()
                        .parentProduct(virtualRoot)
                        .childProduct(parentProduct)
                        .childCompositionRatio(1)
                        .build(),
                BomTree.builder()
                        .parentProduct(parentProduct)
                        .childProduct(childProduct)
                        .childCompositionRatio(2)
                        .build()
        );

        Map<Long, List<BomTree>> bomTreeMap = new HashMap<>();
        bomTreeMap.put(-1L, List.of(bomTrees.get(0)));
        bomTreeMap.put(1L, List.of(bomTrees.get(1)));

        UnifiedBomDataDto unifiedBomData = new UnifiedBomDataDto(
                virtualRoot,
                bomTrees,
                bomTreeMap
        );

        MrpContextDto context = MrpContextDto.builder()
                .inventoryRecord(inventoryRecordMap)
                .computedDate(LocalDate.now())
                .build();

        when(mrpCalculatorUseCase.calculate(any(MrpNodeDto.class), any(MrpContextDto.class)))
                .thenReturn(new MrpCalculateResultDto(
                        0,
                        List.of(PurchaseOrderReport.builder().quantity(2L).build()),
                        List.of(ProductionPlanningReport.builder().quantity(1L).build()),
                        new ArrayList<>(),
                        5
                ));

        // when
        MrpCalculateResultDto result = mrpBomTreeTraversalService.traverse(
                command,
                unifiedBomData,
                context
        );

        // then
        assertThat(result.purchaseOrderReports()).hasSize(2);
        assertThat(result.productionPlanningReports()).hasSize(1);
        assertThat(result.mrpExceptionReports()).isEmpty();

        assertThat(result.purchaseOrderReports().get(0))
                .satisfies(report ->
                        assertThat(report.getQuantity()).isEqualTo(2)
                );

        assertThat(result.productionPlanningReports().get(0))
                .satisfies(report ->
                        assertThat(report.getQuantity()).isEqualTo(1)
                );
    }

    @Test
    @DisplayName("중복된 제품이 있는 BOM 트리를 순회하여 처리한다")
    void traverse_withDuplicateProducts_shouldHandleDuplicates() {
        // given
        Map<String, Integer> engineCountMap = Map.of("kappa", 1);
        MrpInputCommand command = new MrpInputCommand(engineCountMap, dueDate);

        Product duplicateProduct = Product.builder()
                .productId(2L)
                .productNumber("CHILD-001")  // 같은 제품 번호
                .productName("Duplicate Child")
                .company(vendorCompany)
                .build();

        List<BomTree> bomTrees = List.of(
                BomTree.builder()
                        .parentProduct(virtualRoot)
                        .childProduct(parentProduct)
                        .childCompositionRatio(1)
                        .build(),
                BomTree.builder()
                        .parentProduct(parentProduct)
                        .childProduct(childProduct)
                        .childCompositionRatio(2)
                        .build(),
                BomTree.builder()
                        .parentProduct(parentProduct)
                        .childProduct(duplicateProduct)
                        .childCompositionRatio(3)
                        .build()
        );

        Map<Long, List<BomTree>> bomTreeMap = new HashMap<>();
        bomTreeMap.put(-1L, List.of(bomTrees.get(0)));
        bomTreeMap.put(1L, List.of(bomTrees.get(1), bomTrees.get(2)));

        UnifiedBomDataDto unifiedBomData = new UnifiedBomDataDto(
                virtualRoot,
                bomTrees,
                bomTreeMap
        );

        MrpContextDto context = MrpContextDto.builder()
                .inventoryRecord(inventoryRecordMap)
                .computedDate(LocalDate.now())
                .build();

        when(mrpCalculatorUseCase.calculate(any(MrpNodeDto.class), any(MrpContextDto.class)))
                .thenReturn(new MrpCalculateResultDto(
                        0,
                        List.of(PurchaseOrderReport.builder().quantity(5L).build()),
                        List.of(ProductionPlanningReport.builder().quantity(1L).build()),
                        new ArrayList<>(),
                        5
                ));

        // when
        MrpCalculateResultDto result = mrpBomTreeTraversalService.traverse(
                command,
                unifiedBomData,
                context
        );

        // then
        assertThat(result.purchaseOrderReports()).hasSize(3);
        assertThat(result.productionPlanningReports()).hasSize(1);
        assertThat(result.mrpExceptionReports()).isEmpty();
    }

    @Test
    @DisplayName("MRP 예외가 발생했을 때 문제 노드를 생성하고 적절한 해결책을 제시한다")
    void traverse_withMrpExceptions_shouldCreateProblemNodesAndSolutions() {
        // given
        Map<String, Integer> engineCountMap = Map.of("kappa", 1);
        MrpInputCommand command = new MrpInputCommand(engineCountMap, dueDate);

        List<BomTree> bomTrees = List.of(
                BomTree.builder()
                        .parentProduct(virtualRoot)
                        .childProduct(parentProduct)
                        .childCompositionRatio(1)
                        .build(),
                BomTree.builder()
                        .parentProduct(parentProduct)
                        .childProduct(childProduct)
                        .childCompositionRatio(2)
                        .build()
        );

        Map<Long, List<BomTree>> bomTreeMap = new HashMap<>();
        bomTreeMap.put(-1L, List.of(bomTrees.get(0)));
        bomTreeMap.put(1L, List.of(bomTrees.get(1)));

        UnifiedBomDataDto unifiedBomData = new UnifiedBomDataDto(
                virtualRoot,
                bomTrees,
                bomTreeMap
        );

        MrpContextDto context = MrpContextDto.builder()
                .inventoryRecord(inventoryRecordMap)
                .computedDate(LocalDate.now())
                .build();

        // Mock calculator to return an exception for the child product
        when(mrpCalculatorUseCase.calculate(any(MrpNodeDto.class), any(MrpContextDto.class)))
                .thenAnswer(invocation -> {
                    MrpNodeDto node = invocation.getArgument(0);
                    if (node.product().getProductNumber().equals("CHILD-001")) {
                        MrpExceptionReport exceptionReport = MrpExceptionReport.builder()
                                .exceptionType("LEAD_TIME_VIOLATION")
                                .exceptionMessage("리드타임이 납기일을 초과합니다")
                                .build();
                        return MrpCalculateResultDto.withException(exceptionReport);
                    }
                    return new MrpCalculateResultDto(
                            0,
                            new ArrayList<>(),
                            new ArrayList<>(),
                            new ArrayList<>(),
                            5
                    );
                });

        // when
        MrpCalculateResultDto result = mrpBomTreeTraversalService.traverse(
                command,
                unifiedBomData,
                context
        );

        // then
        assertThat(result.mrpExceptionReports()).hasSize(1);
        assertThat(result.mrpExceptionReports().get(0))
                .satisfies(report -> {
                    assertThat(report.getExceptionType()).isEqualTo("LEAD_TIME_VIOLATION");
                    assertThat(report.getSolution()).contains("최소 납기일은");
                });
    }

    @Test
    @DisplayName("여러 벤더의 리드타임이 다를 때 최대 리드타임 기준으로 발주일을 조정한다")
    void traverse_withMultipleVendors_shouldAdjustOrderDatesBasedOnMaxLeadTime() {
        // given
        Map<String, Integer> engineCountMap = Map.of("kappa", 1);
        MrpInputCommand command = new MrpInputCommand(engineCountMap, dueDate);

        // Create another vendor product with different lead time
        Product anotherVendorProduct = Product.builder()
                .productId(3L)
                .productNumber("VENDOR-002")
                .productName("Another Vendor Product")
                .company(vendorCompany)
                .bayList(new ArrayList<>())  // bayList 초기화 추가
                .build();

        // Add to inventory record map
        inventoryRecordMap.put(anotherVendorProduct.getProductId(),
                InventoryRecordItem.builder()
                        .inventoryRecord(inventoryRecord)
                        .product(anotherVendorProduct)
                        .stockCount(30L)
                        .compositionRatio(1)
                        .leadTime(15) // 더 긴 리드타임
                        .build()
        );

        List<BomTree> bomTrees = List.of(
                BomTree.builder()
                        .parentProduct(virtualRoot)
                        .childProduct(parentProduct)
                        .childCompositionRatio(1)
                        .build(),
                BomTree.builder()
                        .parentProduct(parentProduct)
                        .childProduct(childProduct)
                        .childCompositionRatio(2)
                        .build(),
                BomTree.builder()
                        .parentProduct(parentProduct)
                        .childProduct(anotherVendorProduct)
                        .childCompositionRatio(1)
                        .build()
        );

        Map<Long, List<BomTree>> bomTreeMap = new HashMap<>();
        bomTreeMap.put(-1L, List.of(bomTrees.get(0)));
        bomTreeMap.put(1L, List.of(bomTrees.get(1), bomTrees.get(2)));

        UnifiedBomDataDto unifiedBomData = new UnifiedBomDataDto(
                virtualRoot,
                bomTrees,
                bomTreeMap
        );

        LocalDate computedDate = LocalDate.now();
        MrpContextDto context = MrpContextDto.builder()
                .inventoryRecord(inventoryRecordMap)
                .computedDate(computedDate)
                .build();

        // Mock calculator to return different lead times for vendor products
        when(mrpCalculatorUseCase.calculate(any(MrpNodeDto.class), any(MrpContextDto.class)))
                .thenAnswer(invocation -> {
                    MrpNodeDto node = invocation.getArgument(0);
                    if (node.product().getCompany().getIsVendor()) {
                        int leadTime = node.product().getProductNumber().equals("VENDOR-002") ? 15 : 5;
                        return new MrpCalculateResultDto(
                                0,
                                List.of(PurchaseOrderReport.builder()
                                        .product(node.product())
                                        .purchaseOrderDate(computedDate)
                                        .quantity(node.requiredPartsCount())
                                        .build()),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                leadTime
                        );
                    }
                    return new MrpCalculateResultDto(
                            0,
                            new ArrayList<>(),
                            List.of(ProductionPlanningReport.builder()
                                    .product(node.product())
                                    .quantity(1L)
                                    .build()),
                            new ArrayList<>(),
                            0
                    );
                });

        // when
        MrpCalculateResultDto result = mrpBomTreeTraversalService.traverse(
                command,
                unifiedBomData,
                context
        );

        // then
        assertThat(result.purchaseOrderReports()).hasSize(2);

        // All purchase orders should be adjusted to the longest lead time
        for (PurchaseOrderReport report : result.purchaseOrderReports()) {
            assertThat(report.getPurchaseOrderDate())
                    .isEqualTo(computedDate.minusDays(15));
        }
    }

    @Test
    @DisplayName("빈 자식 노드 리스트를 가진 제품을 처리할 수 있다")
    void traverse_withEmptyChildNodes_shouldHandleGracefully() {
        // given
        Map<String, Integer> engineCountMap = Map.of("kappa", 1);
        MrpInputCommand command = new MrpInputCommand(engineCountMap, dueDate);

        Product leafProduct = Product.builder()
                .productId(3L)
                .productNumber("LEAF-001")
                .productName("Leaf Product")
                .company(normalCompany)
                .bayList(new ArrayList<>())  // bayList 초기화 추가
                .build();

        List<BomTree> bomTrees = List.of(
                BomTree.builder()
                        .parentProduct(virtualRoot)
                        .childProduct(leafProduct)
                        .childCompositionRatio(1)
                        .build()
        );

        Map<Long, List<BomTree>> bomTreeMap = new HashMap<>();
        bomTreeMap.put(-1L, List.of(bomTrees.get(0)));
        // Deliberately not adding any children for leafProduct

        UnifiedBomDataDto unifiedBomData = new UnifiedBomDataDto(
                virtualRoot,
                bomTrees,
                bomTreeMap
        );

        MrpContextDto context = MrpContextDto.builder()
                .inventoryRecord(inventoryRecordMap)
                .computedDate(LocalDate.now())
                .build();

        when(mrpCalculatorUseCase.calculate(any(MrpNodeDto.class), any(MrpContextDto.class)))
                .thenReturn(new MrpCalculateResultDto(
                        0,
                        List.of(PurchaseOrderReport.builder().quantity(1L).build()),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        5
                ));

        // when
        MrpCalculateResultDto result = mrpBomTreeTraversalService.traverse(
                command,
                unifiedBomData,
                context
        );

        // then
        assertThat(result.purchaseOrderReports()).hasSize(1);
        assertThat(result.productionPlanningReports()).isEmpty();
        assertThat(result.mrpExceptionReports()).isEmpty();
    }
}