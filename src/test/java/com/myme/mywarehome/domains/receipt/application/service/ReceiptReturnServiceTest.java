package com.myme.mywarehome.domains.receipt.application.service;

import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.domain.Return;
import com.myme.mywarehome.domains.receipt.application.domain.service.OutboundProductDomainService;
import com.myme.mywarehome.domains.receipt.application.port.in.event.ReceiptPlanStatusChangedEvent;
import com.myme.mywarehome.domains.receipt.application.port.out.CreateReturnPort;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReceiptReturnServiceTest {

    @InjectMocks
    private ReceiptReturnService receiptReturnService;

    @Mock
    private OutboundProductDomainService outboundProductDomainService;

    @Mock
    private CreateReturnPort createReturnPort;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Captor
    private ArgumentCaptor<Return> returnCaptor;

    @Captor
    private ArgumentCaptor<ReceiptPlanStatusChangedEvent> statusChangedEventCaptor;

    private ReceiptPlan receiptPlan;
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
    }

    @Test
    @DisplayName("반품 처리를 정상적으로 수행한다")
    void process_WithValidData_ProcessesReturnSuccessfully() {
        // given
        when(outboundProductDomainService.validateAndCreateOutboundProduct(outboundProductId))
                .thenReturn(receiptPlan);

        // when
        receiptReturnService.process(outboundProductId, today);

        // then
        verify(createReturnPort).create(returnCaptor.capture());
        verify(eventPublisher).publishEvent(statusChangedEventCaptor.capture());

        // 생성된 Return 객체 검증
        Return capturedReturn = returnCaptor.getValue();
        assertThat(capturedReturn.getReceiptPlan()).isEqualTo(receiptPlan);
        assertThat(capturedReturn.getReturnDate()).isEqualTo(today);

        // 발행된 이벤트 검증
        ReceiptPlanStatusChangedEvent statusEvent = statusChangedEventCaptor.getValue();
        assertThat(statusEvent.receiptPlanId()).isEqualTo(receiptPlan.getReceiptPlanId());
        assertThat(statusEvent.selectedDate()).isEqualTo(today);
        assertThat(statusEvent.eventType()).isEqualTo("RECEIPT_PROCESSED");
    }

    @Test
    @DisplayName("OutboundProduct 검증을 수행한다")
    void process_ValidatesOutboundProduct() {
        // given
        when(outboundProductDomainService.validateAndCreateOutboundProduct(outboundProductId))
                .thenReturn(receiptPlan);

        // when
        receiptReturnService.process(outboundProductId, today);

        // then
        verify(outboundProductDomainService).validateAndCreateOutboundProduct(outboundProductId);
    }

    @Test
    @DisplayName("반품 처리 후 상태 변경 이벤트를 발행한다")
    void process_PublishesStatusChangedEvent() {
        // given
        when(outboundProductDomainService.validateAndCreateOutboundProduct(outboundProductId))
                .thenReturn(receiptPlan);

        // when
        receiptReturnService.process(outboundProductId, today);

        // then
        verify(eventPublisher).publishEvent(statusChangedEventCaptor.capture());

        ReceiptPlanStatusChangedEvent event = statusChangedEventCaptor.getValue();
        assertThat(event.receiptPlanId()).isEqualTo(receiptPlan.getReceiptPlanId());
        assertThat(event.selectedDate()).isEqualTo(today);
        assertThat(event.eventType()).isEqualTo("RECEIPT_PROCESSED");
    }

    @Test
    @DisplayName("반품 엔티티를 생성하고 저장한다")
    void process_CreatesAndSavesReturnEntity() {
        // given
        when(outboundProductDomainService.validateAndCreateOutboundProduct(outboundProductId))
                .thenReturn(receiptPlan);

        // when
        receiptReturnService.process(outboundProductId, today);

        // then
        verify(createReturnPort).create(returnCaptor.capture());

        Return capturedReturn = returnCaptor.getValue();
        assertThat(capturedReturn).isNotNull();
        assertThat(capturedReturn.getReceiptPlan()).isEqualTo(receiptPlan);
        assertThat(capturedReturn.getReturnDate()).isEqualTo(today);
    }
}