package com.myme.mywarehome.domains.mrp.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.myme.mywarehome.domains.company.application.domain.Company;
import com.myme.mywarehome.domains.mrp.application.domain.BomTree;
import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecord;
import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecordItem;
import com.myme.mywarehome.domains.mrp.application.domain.ProductionPlanningReport;
import com.myme.mywarehome.domains.mrp.application.domain.PurchaseOrderReport;
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
}