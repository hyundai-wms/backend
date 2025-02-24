package com.myme.mywarehome.domains.mrp.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.myme.mywarehome.domains.mrp.application.domain.BomTree;
import com.myme.mywarehome.domains.mrp.application.domain.MrpOutput;
import com.myme.mywarehome.domains.mrp.application.domain.ProductionPlanningReport;
import com.myme.mywarehome.domains.mrp.application.domain.PurchaseOrderReport;
import com.myme.mywarehome.domains.mrp.application.exception.MrpCannotOrderException;
import com.myme.mywarehome.domains.mrp.application.exception.MrpOutputNotFoundException;
import com.myme.mywarehome.domains.mrp.application.port.in.event.CreatePlanFromMrpEvent;
import com.myme.mywarehome.domains.mrp.application.port.in.event.UpdateSafetyStockFromMrpEvent;
import com.myme.mywarehome.domains.mrp.application.port.out.GetAllProductPort;
import com.myme.mywarehome.domains.mrp.application.port.out.GetBomTreePort;
import com.myme.mywarehome.domains.mrp.application.port.out.GetMrpOutputPort;
import com.myme.mywarehome.domains.mrp.application.port.out.UpdateMrpOutputPort;
import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.stock.adapter.in.event.event.StockBulkUpdateEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class MrpOrderServiceTest {

    @Mock
    private GetMrpOutputPort getMrpOutputPort;

    @Mock
    private GetBomTreePort getBomTreePort;

    @Mock
    private UpdateMrpOutputPort updateMrpOutputPort;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private GetAllProductPort getAllProductPort;

    @Captor
    private ArgumentCaptor<CreatePlanFromMrpEvent> createPlanEventCaptor;

    @Captor
    private ArgumentCaptor<UpdateSafetyStockFromMrpEvent> updateSafetyStockEventCaptor;

    @Captor
    private ArgumentCaptor<StockBulkUpdateEvent> stockBulkUpdateEventCaptor;

    private MrpOrderService mrpOrderService;
    private MrpOutput mrpOutput;
    private Product parentProduct;
    private Product childProduct;
    private LocalDate dueDate;

    @BeforeEach
    void setUp() {
        mrpOrderService = new MrpOrderService(
                getMrpOutputPort,
                getBomTreePort,
                updateMrpOutputPort,
                eventPublisher,
                getAllProductPort
        );

        dueDate = LocalDate.now().plusDays(30);

        parentProduct = Product.builder()
                .productId(1L)
                .productNumber("PARENT-001")
                .productName("Parent Product")
                .build();

        childProduct = Product.builder()
                .productId(2L)
                .productNumber("CHILD-001")
                .productName("Child Product")
                .build();

        mrpOutput = MrpOutput.builder()
                .mrpOutputId(1L)
                .mrpOutputCode("MRP-20240224-001")
                .createdDate(LocalDate.now())
                .dueDate(dueDate)
                .canOrder(true)
                .isOrdered(false)
                .kappaCount(50)
                .gammaCount(25)
                .build();
    }

    @Test
    @DisplayName("MRP 발주를 정상적으로 수행한다")
    void run_withValidMrpOutput_shouldCreatePlansAndPublishEvents() {
        // given
        when(getMrpOutputPort.getMrpOutputByMrpOutputId(1L))
                .thenReturn(Optional.of(mrpOutput));

        List<PurchaseOrderReport> purchaseReports = new ArrayList<>();
        purchaseReports.add(PurchaseOrderReport.builder()
                .product(childProduct)
                .quantity(100L)
                .receiptPlanDate(dueDate)
                .build());
        mrpOutput.assignWithPurchaseOrderReports(purchaseReports);

        List<ProductionPlanningReport> productionReports = new ArrayList<>();
        productionReports.add(ProductionPlanningReport.builder()
                .product(parentProduct)
                .quantity(50L)
                .productionPlanningDate(dueDate.minusDays(5))
                .receiptPlanDate(dueDate)
                .build());
        mrpOutput.assignWithProductionPlanningReports(productionReports);

        BomTree bomTree = BomTree.builder()
                .parentProduct(parentProduct)
                .childProduct(childProduct)
                .childCompositionRatio(2)
                .build();

        when(getBomTreePort.findAllByParentNumber(parentProduct.getProductNumber()))
                .thenReturn(List.of(bomTree));

        when(getAllProductPort.getAllProductNumbers())
                .thenReturn(List.of("PARENT-001", "CHILD-001"));

        // when
        mrpOrderService.run(1L);

        // then
        verify(eventPublisher).publishEvent(createPlanEventCaptor.capture());
        verify(eventPublisher).publishEvent(updateSafetyStockEventCaptor.capture());
        verify(eventPublisher).publishEvent(stockBulkUpdateEventCaptor.capture());
        verify(updateMrpOutputPort).orderSuccess(mrpOutput);

        CreatePlanFromMrpEvent createPlanEvent = createPlanEventCaptor.getValue();
        assertThat(createPlanEvent.receiptPlanCommands())
                .hasSize(2)
                .anySatisfy(command -> {
                    assertThat(command.productNumber()).isEqualTo("CHILD-001");
                    assertThat(command.itemCount()).isEqualTo(100);
                    assertThat(command.receiptPlanDate()).isEqualTo(dueDate);
                })
                .anySatisfy(command -> {
                    assertThat(command.productNumber()).isEqualTo("PARENT-001");
                    assertThat(command.itemCount()).isEqualTo(50);
                    assertThat(command.receiptPlanDate()).isEqualTo(dueDate);
                });

        assertThat(createPlanEvent.issuePlanCommands())
                .hasSizeGreaterThanOrEqualTo(3)  // 하위 부품 출고 + 엔진 출고
                .anySatisfy(command -> {
                    assertThat(command.productNumber()).isEqualTo("CHILD-001");
                    assertThat(command.itemCount()).isEqualTo(100);  // 50 * 2
                })
                .anySatisfy(command -> {
                    assertThat(command.productNumber()).isEqualTo("10000-03P00");  // Kappa
                    assertThat(command.itemCount()).isEqualTo(2);  // ceil(50/25)
                })
                .anySatisfy(command -> {
                    assertThat(command.productNumber()).isEqualTo("10000-04P00");  // Gamma
                    assertThat(command.itemCount()).isEqualTo(1);  // ceil(25/25)
                });

        StockBulkUpdateEvent stockEvent = stockBulkUpdateEventCaptor.getValue();
        assertThat(stockEvent.productNumberList())
                .containsExactlyInAnyOrder("PARENT-001", "CHILD-001");

        // MRP 상태 변경 검증
        assertThat(mrpOutput.getIsOrdered()).isTrue();
        assertThat(mrpOutput.getCanOrder()).isFalse();
        assertThat(mrpOutput.getOrderedDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("발주 불가능한 MRP의 경우 예외가 발생한다")
    void run_whenMrpCannotBeOrdered_shouldThrowException() {
        // given
        mrpOutput = MrpOutput.builder()
                .mrpOutputId(1L)
                .mrpOutputCode("MRP-20240224-001")
                .createdDate(LocalDate.now())
                .dueDate(dueDate)
                .canOrder(false)
                .isOrdered(false)
                .build();

        when(getMrpOutputPort.getMrpOutputByMrpOutputId(1L))
                .thenReturn(Optional.of(mrpOutput));

        // when, then
        assertThatThrownBy(() -> mrpOrderService.run(1L))
                .isInstanceOf(MrpCannotOrderException.class);
    }

    @Test
    @DisplayName("존재하지 않는 MRP ID로 조회 시 예외가 발생한다")
    void run_withInvalidMrpId_shouldThrowException() {
        // given
        when(getMrpOutputPort.getMrpOutputByMrpOutputId(999L))
                .thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> mrpOrderService.run(999L))
                .isInstanceOf(MrpOutputNotFoundException.class);
    }
}