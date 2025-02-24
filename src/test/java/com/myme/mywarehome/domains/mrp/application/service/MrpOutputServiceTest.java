package com.myme.mywarehome.domains.mrp.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.myme.mywarehome.domains.mrp.application.domain.MrpExceptionReport;
import com.myme.mywarehome.domains.mrp.application.domain.MrpOutput;
import com.myme.mywarehome.domains.mrp.application.domain.ProductionPlanningReport;
import com.myme.mywarehome.domains.mrp.application.domain.PurchaseOrderReport;
import com.myme.mywarehome.domains.mrp.application.port.in.command.MrpInputCommand;
import com.myme.mywarehome.domains.mrp.application.port.out.CreateMrpOutputPort;
import com.myme.mywarehome.domains.mrp.application.port.out.CreateMrpReportFilePort;
import com.myme.mywarehome.domains.mrp.application.port.out.UpdateMrpOutputPort;
import com.myme.mywarehome.domains.mrp.application.service.dto.MrpCalculateResultDto;
import com.myme.mywarehome.domains.product.application.domain.Product;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MrpOutputServiceTest {

    @Mock
    private CreateMrpOutputPort createMrpOutputPort;

    @Mock
    private UpdateMrpOutputPort updateMrpOutputPort;

    @Mock
    private CreateMrpReportFilePort createMrpReportFilePort;

    @Captor
    private ArgumentCaptor<MrpOutput> mrpOutputCaptor;

    private MrpOutputService mrpOutputService;
    private LocalDate dueDate;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        mrpOutputService = new MrpOutputService(
                createMrpOutputPort,
                updateMrpOutputPort,
                createMrpReportFilePort
        );
        ReflectionTestUtils.setField(mrpOutputService, "activeProfile", "test");

        dueDate = LocalDate.now().plusDays(30);

        product1 = Product.builder()
                .productId(1L)
                .productNumber("PROD-001")
                .productName("Product 1")
                .build();

        product2 = Product.builder()
                .productId(1L)
                .productNumber("PROD-001")
                .productName("Product 1")
                .build();
    }

    @Test
    @DisplayName("정상적인 MRP 계산 결과를 저장한다")
    void saveResults_withValidResult_shouldSaveAndOptimize() {
        // given
        Map<String, Integer> engineCountMap = Map.of(
                "kappa", 50,
                "gamma", 25
        );
        MrpInputCommand command = new MrpInputCommand(engineCountMap, dueDate);

        LocalDate earlierDate = dueDate.minusDays(5);
        LocalDate laterDate = dueDate.minusDays(3);

        List<PurchaseOrderReport> purchaseReports = List.of(
                PurchaseOrderReport.builder()
                        .product(product1)
                        .quantity(100L)
                        .safeItemCount(10)
                        .purchaseOrderDate(earlierDate)
                        .receiptPlanDate(dueDate)
                        .build(),
                PurchaseOrderReport.builder()
                        .product(product1)
                        .quantity(50L)
                        .safeItemCount(5)
                        .purchaseOrderDate(laterDate)
                        .receiptPlanDate(dueDate)
                        .build()
        );

        List<ProductionPlanningReport> productionReports = List.of(
                ProductionPlanningReport.builder()
                        .product(product2)
                        .quantity(80L)
                        .safeItemCount(8)
                        .productionPlanningDate(earlierDate)
                        .receiptPlanDate(dueDate)
                        .build(),
                ProductionPlanningReport.builder()
                        .product(product2)
                        .quantity(40L)
                        .safeItemCount(4)
                        .productionPlanningDate(laterDate)
                        .receiptPlanDate(dueDate)
                        .build()
        );

        MrpCalculateResultDto result = new MrpCalculateResultDto(
                0,
                purchaseReports,
                productionReports,
                List.of(),
                0
        );

        // when
        mrpOutputService.saveResults(command, result);

        // then
        verify(updateMrpOutputPort).deactivatePreviousOrders();
        verify(createMrpOutputPort).createMrpOutput(
                mrpOutputCaptor.capture(),
                any(),
                any(),
                any()
        );

        MrpOutput savedOutput = mrpOutputCaptor.getValue();
        assertThat(savedOutput.getDueDate()).isEqualTo(dueDate);
        assertThat(savedOutput.getCanOrder()).isTrue();
        assertThat(savedOutput.getIsOrdered()).isFalse();
        assertThat(savedOutput.getKappaCount()).isEqualTo(50);
        assertThat(savedOutput.getGammaCount()).isEqualTo(25);

        // 최적화된 발주 보고서 검증
        assertThat(savedOutput.getPurchaseOrderReportList())
                .hasSize(1)
                .satisfies(reports -> {
                    PurchaseOrderReport optimized = reports.get(0);
                    assertThat(optimized.getProduct()).isEqualTo(product1);
                    assertThat(optimized.getQuantity()).isEqualTo(150L);  // 100 + 50
                    assertThat(optimized.getSafeItemCount()).isEqualTo(15);  // 10 + 5
                    assertThat(optimized.getPurchaseOrderDate()).isEqualTo(earlierDate);
                    assertThat(optimized.getReceiptPlanDate()).isEqualTo(dueDate);
                });

        // 최적화된 생산 계획 보고서 검증
        assertThat(savedOutput.getProductionPlanningReportList())
                .hasSize(1)
                .satisfies(reports -> {
                    ProductionPlanningReport optimized = reports.get(0);
                    assertThat(optimized.getProduct()).isEqualTo(product2);
                    assertThat(optimized.getQuantity()).isEqualTo(120L);  // 80 + 40
                    assertThat(optimized.getSafeItemCount()).isEqualTo(12);  // 8 + 4
                    assertThat(optimized.getProductionPlanningDate()).isEqualTo(earlierDate);
                    assertThat(optimized.getReceiptPlanDate()).isEqualTo(dueDate);
                });
    }

    @Test
    @DisplayName("예외가 있는 MRP 계산 결과를 처리한다")
    void saveResults_withExceptions_shouldSaveExceptionReports() {
        // given
        Map<String, Integer> engineCountMap = Map.of(
                "kappa", 50,
                "gamma", 25
        );
        MrpInputCommand command = new MrpInputCommand(engineCountMap, dueDate);

        List<MrpExceptionReport> exceptionReports = List.of(
                MrpExceptionReport.builder()
                        .exceptionType("LEAD_TIME_VIOLATION")
                        .exceptionMessage("제품 PROD-001는 30일의 리드타임이 필요합니다")
                        .build(),
                MrpExceptionReport.builder()
                        .exceptionType("BIN_CAPACITY_EXCEEDED")
                        .exceptionMessage("제품 PROD-001는 Bin 용량을 초과했습니다")
                        .build()
        );

        MrpCalculateResultDto result = new MrpCalculateResultDto(
                -1,
                List.of(),
                List.of(),
                exceptionReports,
                0
        );

        // when
        mrpOutputService.saveResults(command, result);

        // then
        verify(updateMrpOutputPort).deactivatePreviousOrders();
        verify(createMrpOutputPort).createMrpOutput(
                mrpOutputCaptor.capture(),
                any(),
                any(),
                any()
        );

        MrpOutput savedOutput = mrpOutputCaptor.getValue();
        assertThat(savedOutput.getDueDate()).isEqualTo(dueDate);
        assertThat(savedOutput.getCanOrder()).isFalse();
        assertThat(savedOutput.getIsOrdered()).isFalse();

        // 최적화된 예외 보고서 검증 (BIN_CAPACITY_EXCEEDED 우선)
        assertThat(savedOutput.getMrpExceptionReportList())
                .hasSize(1)
                .satisfies(reports -> {
                    MrpExceptionReport optimized = reports.get(0);
                    assertThat(optimized.getExceptionType()).isEqualTo("BIN_CAPACITY_EXCEEDED");
                    assertThat(optimized.getExceptionMessage()).contains("PROD-001");
                });
    }

    @Test
    @DisplayName("local 프로필에서는 파일을 생성하지 않는다")
    void saveResults_whenLocalProfile_shouldNotCreateFiles() {
        // given
        ReflectionTestUtils.setField(mrpOutputService, "activeProfile", "local");

        Map<String, Integer> engineCountMap = Map.of("kappa", 50);
        MrpInputCommand command = new MrpInputCommand(engineCountMap, dueDate);

        MrpCalculateResultDto result = new MrpCalculateResultDto(
                0,
                List.of(),
                List.of(),
                List.of(),
                0
        );

        // when
        mrpOutputService.saveResults(command, result);

        // then
        verify(createMrpReportFilePort, never()).createAndUploadPurchaseOrderReport(any(), any());
        verify(createMrpReportFilePort, never()).createAndUploadProductionPlanningReport(any(), any());
        verify(createMrpReportFilePort, never()).createAndUploadMrpExceptionReport(any(), any());
    }
}