package com.myme.mywarehome.domains.receipt.application.service;

import com.myme.mywarehome.domains.receipt.application.domain.Receipt;
import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.domain.service.OutboundProductDomainService;
import com.myme.mywarehome.domains.receipt.application.exception.ReceiptBulkProcessException;
import com.myme.mywarehome.domains.receipt.application.port.in.command.ReceiptProcessBulkCommand;
import com.myme.mywarehome.domains.receipt.application.port.in.event.*;
import com.myme.mywarehome.domains.receipt.application.port.out.*;
import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.stock.adapter.in.event.event.*;
import com.myme.mywarehome.domains.stock.application.domain.Stock;
import com.myme.mywarehome.domains.stock.application.exception.StockCreationTimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiptProcessServiceTest {

    @InjectMocks
    private ReceiptProcessService receiptProcessService;

    @Mock
    private OutboundProductDomainService outboundProductDomainService;

    @Mock
    private CreateReceiptPort createReceiptPort;

    @Mock
    private CreateReturnPort createReturnPort;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private GetReceiptPlanPort getReceiptPlanPort;

    @Mock
    private GetOutboundProductPort getOutboundProductPort;

    @Mock
    private GetReceiptPort getReceiptPort;

    @Mock
    private GetReturnPort getReturnPort;

    @Captor
    private ArgumentCaptor<ReceiptCreatedEvent> receiptCreatedEventCaptor;

    @Captor
    private ArgumentCaptor<ReceiptPlanStatusChangedEvent> statusChangedEventCaptor;

    @Captor
    private ArgumentCaptor<StockUpdateEvent> stockUpdateEventCaptor;

    @Captor
    private ArgumentCaptor<BayUpdateEvent> bayUpdateEventCaptor;

    private ReceiptPlan receiptPlan;
    private Receipt receipt;
    private Stock stock;
    private Product product;
    private LocalDate today;
    private String outboundProductId;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        outboundProductId = "OP-001";

        product = Product.builder()
                .productNumber("TEST001")
                .productName("테스트 상품")
                .build();

        receiptPlan = ReceiptPlan.builder()
                .receiptPlanId(1L)
                .receiptPlanCode("RP001")
                .receiptPlanItemCount(10)
                .product(product)
                .build();

        receipt = Receipt.builder()
                .receiptId(1L)
                .receiptPlan(receiptPlan)
                .receiptDate(today)
                .build();

        stock = Stock.builder()
                .stockId(1L)
                .build();
    }

    @Test
    @DisplayName("입고 처리를 정상적으로 수행한다")
    void process_WithValidData_ProcessesReceiptSuccessfully() {
        // given
        when(outboundProductDomainService.validateAndCreateOutboundProduct(outboundProductId))
                .thenReturn(receiptPlan);
        when(createReceiptPort.create(any(Receipt.class))).thenReturn(receipt);

        // CompletableFuture 설정
        doAnswer(invocation -> {
            ReceiptCreatedEvent event = invocation.getArgument(0);
            event.result().complete(stock);
            return null;
        }).when(eventPublisher).publishEvent(any(ReceiptCreatedEvent.class));

        // when
        Stock result = receiptProcessService.process(outboundProductId, today);

        // then
        assertThat(result).isEqualTo(stock);

        // 이벤트 발행 검증
        verify(eventPublisher).publishEvent(receiptCreatedEventCaptor.capture());
        verify(eventPublisher).publishEvent(statusChangedEventCaptor.capture());
        verify(eventPublisher).publishEvent(stockUpdateEventCaptor.capture());
        verify(eventPublisher).publishEvent(bayUpdateEventCaptor.capture());

        // 이벤트 내용 검증
        ReceiptCreatedEvent receiptEvent = receiptCreatedEventCaptor.getValue();
        assertThat(receiptEvent.receipt()).isEqualTo(receipt);

        ReceiptPlanStatusChangedEvent statusEvent = statusChangedEventCaptor.getValue();
        assertThat(statusEvent.receiptPlanId()).isEqualTo(receiptPlan.getReceiptPlanId());
        assertThat(statusEvent.eventType()).isEqualTo("RECEIPT_PROCESSED");

        StockUpdateEvent stockEvent = stockUpdateEventCaptor.getValue();
        assertThat(stockEvent.productNumber()).isEqualTo(product.getProductNumber());

        BayUpdateEvent bayEvent = bayUpdateEventCaptor.getValue();
        assertThat(bayEvent.productNumber()).isEqualTo(product.getProductNumber());
    }

    @Test
    @DisplayName("재고 생성 시간 초과시 예외가 발생한다")
    void process_WithStockCreationTimeout_ThrowsException() {
        // given
        when(outboundProductDomainService.validateAndCreateOutboundProduct(outboundProductId))
                .thenReturn(receiptPlan);
        when(createReceiptPort.create(any(Receipt.class))).thenReturn(receipt);

        // CompletableFuture 타임아웃 시뮬레이션
        doAnswer(invocation -> {
            ReceiptCreatedEvent event = invocation.getArgument(0);
            CompletableFuture<Stock> future = event.result();
            future.completeExceptionally(new Exception("Timeout"));
            return null;
        }).when(eventPublisher).publishEvent(any(ReceiptCreatedEvent.class));

        // when & then
        assertThatThrownBy(() -> receiptProcessService.process(outboundProductId, today))
                .isInstanceOf(StockCreationTimeoutException.class);
    }

    @Test
    @DisplayName("벌크 입고 처리를 정상적으로 수행한다")
    void processBulk_WithValidData_ProcessesReceiptBulkSuccessfully() {
        // given
        Map<String, Double> returnRates = Map.of(product.getProductNumber(), 20.0);
        ReceiptProcessBulkCommand command = new ReceiptProcessBulkCommand(returnRates);

        when(getReceiptPlanPort.findAllReceiptPlansByDate(today))
                .thenReturn(List.of(receiptPlan));
        when(getOutboundProductPort.findOutboundProductIdsByReceiptPlanId(receiptPlan.getReceiptPlanId()))
                .thenReturn(List.of());
        when(getOutboundProductPort.countByReceiptPlanId(receiptPlan.getReceiptPlanId()))
                .thenReturn(0L);
        when(getReceiptPort.countByReceiptPlanId(receiptPlan.getReceiptPlanId()))
                .thenReturn(0L);
        when(getReturnPort.countByReceiptPlanId(receiptPlan.getReceiptPlanId()))
                .thenReturn(0L);
        when(createReceiptPort.create(any(Receipt.class))).thenReturn(receipt);

        // when
        receiptProcessService.processBulk(command, today);

        // then
        verify(createReceiptPort, atLeastOnce()).create(any(Receipt.class));
        verify(eventPublisher).publishEvent(any(ReceiptBulkCreatedEvent.class));
        verify(eventPublisher).publishEvent(any(ReceiptPlanBulkStatusChangedEvent.class));
        verify(eventPublisher).publishEvent(any(StockBulkUpdateEvent.class));
        verify(eventPublisher).publishEvent(any(BayBulkUpdateEvent.class));
    }

    @Test
    @DisplayName("벌크 처리 중 예외 발생시 ReceiptBulkProcessException이 발생한다")
    void processBulk_WithError_ThrowsReceiptBulkProcessException() {
        // given
        Map<String, Double> returnRates = Map.of(product.getProductNumber(), 20.0);
        ReceiptProcessBulkCommand command = new ReceiptProcessBulkCommand(returnRates);

        when(getReceiptPlanPort.findAllReceiptPlansByDate(today))
                .thenReturn(List.of(receiptPlan));
        when(getOutboundProductPort.findOutboundProductIdsByReceiptPlanId(any()))
                .thenThrow(new ReceiptBulkProcessException());

        // when & then
        assertThatThrownBy(() -> receiptProcessService.processBulk(command, today))
                .isInstanceOf(ReceiptBulkProcessException.class);
    }
}