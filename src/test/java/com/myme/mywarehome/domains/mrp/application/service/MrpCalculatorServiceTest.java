package com.myme.mywarehome.domains.mrp.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.myme.mywarehome.domains.company.application.domain.Company;
import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecord;
import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecordItem;
import com.myme.mywarehome.domains.mrp.application.service.dto.MrpCalculateResultDto;
import com.myme.mywarehome.domains.mrp.application.service.dto.MrpContextDto;
import com.myme.mywarehome.domains.mrp.application.service.dto.MrpNodeDto;
import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.stock.application.domain.Bay;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MrpCalculatorServiceTest {

    private MrpCalculatorService mrpCalculatorService;
    private Map<Long, InventoryRecordItem> inventoryRecordMap;
    private Company vendorCompany;
    private Company manufacturerCompany;
    private InventoryRecord inventoryRecord;
    private LocalDate computedDate;

    @BeforeEach
    void setUp() {
        mrpCalculatorService = new MrpCalculatorService();
        computedDate = LocalDate.now().plusDays(30);  // 30일 후 납기

        vendorCompany = Company.builder()
                .companyId(1L)
                .isVendor(true)
                .build();

        manufacturerCompany = Company.builder()
                .companyId(2L)
                .isVendor(false)
                .build();

        inventoryRecord = InventoryRecord.builder()
                .stockStatusAt(LocalDateTime.now())
                .build();

        inventoryRecordMap = new HashMap<>();
    }

    @Test
    @DisplayName("벤더 제품에 대해 발주 보고서를 생성한다")
    void calculate_whenVendorProduct_shouldCreatePurchaseOrder() {
        // given
        Product vendorProduct = Product.builder()
                .productId(1L)
                .productNumber("VENDOR-001")
                .productName("Vendor Part")
                .company(vendorCompany)
                .eachCount(1)
                .leadTime(3600)  // 1시간
                .bayList(List.of(Bay.builder().build()))  // 1개의 베이
                .build();

        inventoryRecordMap.put(vendorProduct.getProductId(),
                InventoryRecordItem.builder()
                        .inventoryRecord(inventoryRecord)
                        .product(vendorProduct)
                        .stockCount(5L)
                        .compositionRatio(1)
                        .leadTime(1)
                        .build()
        );

        MrpNodeDto mrpNode = new MrpNodeDto(vendorProduct, 100);  // 100개 필요
        MrpContextDto context = MrpContextDto.builder()
                .inventoryRecord(inventoryRecordMap)
                .computedDate(computedDate)
                .build();

        // when
        MrpCalculateResultDto result = mrpCalculatorService.calculate(mrpNode, context);

        // then
        assertThat(result.purchaseOrderReports())
                .hasSize(1)
                .satisfies(reports -> {
                    var report = reports.get(0);
                    assertThat(report.getProduct()).isEqualTo(vendorProduct);
                    assertThat(report.getQuantity()).isEqualTo(106);  // 필요량 100 + 안전재고 10% - 현재고 5 = 106
                    assertThat(report.getSafeItemCount()).isEqualTo(10);  // 안전재고 10%
                    assertThat(report.getPurchaseOrderDate()).isBefore(report.getReceiptPlanDate());
                });

        assertThat(result.productionPlanningReports()).isEmpty();
    }

    @Test
    @DisplayName("제조사 제품에 대해 생산 계획 보고서를 생성한다")
    void calculate_whenManufacturerProduct_shouldCreateProductionPlan() {
        // given
        Product manufacturerProduct = Product.builder()
                .productId(2L)
                .productNumber("MANU-001")
                .productName("Manufactured Part")
                .company(manufacturerCompany)
                .eachCount(1)
                .leadTime(3600)  // 1시간
                .bayList(List.of(Bay.builder().build()))  // 1개의 베이
                .build();

        inventoryRecordMap.put(manufacturerProduct.getProductId(),
                InventoryRecordItem.builder()
                        .inventoryRecord(inventoryRecord)
                        .product(manufacturerProduct)
                        .stockCount(5L)
                        .compositionRatio(1)
                        .leadTime(1)
                        .build()
        );

        MrpNodeDto mrpNode = new MrpNodeDto(manufacturerProduct, 100);  // 100개 필요
        MrpContextDto context = MrpContextDto.builder()
                .inventoryRecord(inventoryRecordMap)
                .computedDate(computedDate)
                .build();

        // when
        MrpCalculateResultDto result = mrpCalculatorService.calculate(mrpNode, context);

        // then
        assertThat(result.productionPlanningReports())
                .hasSize(1)
                .satisfies(reports -> {
                    var report = reports.get(0);
                    assertThat(report.getProduct()).isEqualTo(manufacturerProduct);
                    assertThat(report.getQuantity()).isEqualTo(106);  // 필요량 100 + 안전재고 10% - 현재고 5 = 106
                    assertThat(report.getSafeItemCount()).isEqualTo(10);  // 안전재고 10%
                    assertThat(report.getProductionPlanningDate()).isBefore(report.getReceiptPlanDate());
                });

        assertThat(result.purchaseOrderReports()).isEmpty();
    }

    @Test
    @DisplayName("리드타임이 납기를 초과하면 예외 보고서를 생성한다")
    void calculate_whenLeadTimeExceedsDueDate_shouldCreateExceptionReport() {
        // given
        Product vendorProduct = Product.builder()
                .productId(1L)
                .productNumber("VENDOR-001")
                .productName("Vendor Part")
                .company(vendorCompany)
                .eachCount(1)
                .leadTime(3600 * 24 * 60)  // 60일 (납기 30일 초과)
                .bayList(List.of(Bay.builder().build()))
                .build();

        MrpNodeDto mrpNode = new MrpNodeDto(vendorProduct, 100);
        MrpContextDto context = MrpContextDto.builder()
                .inventoryRecord(inventoryRecordMap)
                .computedDate(computedDate)
                .build();

        // when
        MrpCalculateResultDto result = mrpCalculatorService.calculate(mrpNode, context);

        // then
        assertThat(result.mrpExceptionReports())
                .hasSize(2)
                .satisfies(reports -> {
                    var report = reports.get(0);
                    assertThat(report.getExceptionType()).isEqualTo("LEAD_TIME_VIOLATION");
                    assertThat(report.getExceptionMessage()).contains("VENDOR-001", "60일");
                });
    }

    @Test
    @DisplayName("필요 Bin이 가용 Bin을 초과하면 예외 보고서를 생성한다")
    void calculate_whenBinCapacityExceeded_shouldCreateExceptionReport() {
        // given
        Product vendorProduct = Product.builder()
                .productId(1L)
                .productNumber("VENDOR-001")
                .productName("Vendor Part")
                .company(vendorCompany)
                .eachCount(1)
                .leadTime(3600)
                .bayList(List.of(Bay.builder().build()))  // 1개의 베이 (10개의 Bin)
                .build();

        inventoryRecordMap.put(vendorProduct.getProductId(),
                InventoryRecordItem.builder()
                        .inventoryRecord(inventoryRecord)
                        .product(vendorProduct)
                        .stockCount(50L)  // 현재 50개 재고
                        .compositionRatio(1)
                        .leadTime(1)
                        .build()
        );

        MrpNodeDto mrpNode = new MrpNodeDto(vendorProduct, 100);  // 추가 100개 필요 (총 150개로 Bin 용량 초과)
        MrpContextDto context = MrpContextDto.builder()
                .inventoryRecord(inventoryRecordMap)
                .computedDate(computedDate)
                .build();

        // when
        MrpCalculateResultDto result = mrpCalculatorService.calculate(mrpNode, context);

        // then
        assertThat(result.mrpExceptionReports())
                .hasSize(1)
                .satisfies(reports -> {
                    var report = reports.get(0);
                    assertThat(report.getExceptionType()).isEqualTo("BIN_CAPACITY_EXCEEDED");
                    assertThat(report.getExceptionMessage())
                            .contains("VENDOR-001")
                            .contains("Bin");
                });
    }
}