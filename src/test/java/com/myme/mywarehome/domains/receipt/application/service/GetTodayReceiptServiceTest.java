package com.myme.mywarehome.domains.receipt.application.service;

import com.myme.mywarehome.domains.receipt.application.exception.ReceiptNotFoundException;
import com.myme.mywarehome.domains.receipt.application.port.in.result.TodayReceiptResult;
import com.myme.mywarehome.domains.receipt.application.port.out.GetReceiptPlanPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetTodayReceiptServiceTest {

    @InjectMocks
    private GetTodayReceiptService getTodayReceiptService;

    @Mock
    private GetReceiptPlanPort getReceiptPlanPort;

    private LocalDate today;
    private TodayReceiptResult todayReceiptResult;
    private Pageable pageable;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        now = LocalDateTime.now();
        pageable = PageRequest.of(0, 10);

        todayReceiptResult = new TodayReceiptResult(
                1L,                    // receiptPlanId
                "RP001",              // receiptPlanCode
                today,                // receiptPlanDate
                5L,                   // receiptCount
                10L,                  // totalItemCount
                "PENDING",            // receiptStatus
                "TEST001",            // productNumber
                "테스트 상품",          // productName
                1L,                   // companyId
                "COMPANY001",         // companyCode
                "테스트 업체",          // companyName
                now.minusHours(1),    // createdAt
                now                   // updatedAt
        );
    }

    @Test
    @DisplayName("선택한 날짜의 입고 계획 목록을 조회한다")
    void getTodayReceipt_WithSelectedDate_ReturnsTodayReceipts() {
        // given
        List<TodayReceiptResult> receipts = List.of(todayReceiptResult);
        Page<TodayReceiptResult> receiptPage = new PageImpl<>(receipts, pageable, receipts.size());

        when(getReceiptPlanPort.findTodayReceipts(today, pageable))
                .thenReturn(receiptPage);

        // when
        Page<TodayReceiptResult> result = getTodayReceiptService.getTodayReceipt(today, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        TodayReceiptResult foundResult = result.getContent().get(0);
        assertThat(foundResult.receiptPlanId()).isEqualTo(todayReceiptResult.receiptPlanId());
        assertThat(foundResult.receiptPlanCode()).isEqualTo(todayReceiptResult.receiptPlanCode());
        assertThat(foundResult.receiptPlanDate()).isEqualTo(today);
        assertThat(foundResult.receiptCount()).isEqualTo(todayReceiptResult.receiptCount());
        assertThat(foundResult.itemCount()).isEqualTo(todayReceiptResult.itemCount());
        assertThat(foundResult.receiptStatus()).isEqualTo("PENDING");
        assertThat(foundResult.productNumber()).isEqualTo(todayReceiptResult.productNumber());
        assertThat(foundResult.productName()).isEqualTo(todayReceiptResult.productName());
        assertThat(foundResult.companyId()).isEqualTo(todayReceiptResult.companyId());
        assertThat(foundResult.companyCode()).isEqualTo(todayReceiptResult.companyCode());
        assertThat(foundResult.companyName()).isEqualTo(todayReceiptResult.companyName());
    }

    @Test
    @DisplayName("선택한 날짜에 입고 계획이 없으면 빈 페이지를 반환한다")
    void getTodayReceipt_WithNoReceipts_ReturnsEmptyPage() {
        // given
        Page<TodayReceiptResult> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(getReceiptPlanPort.findTodayReceipts(today, pageable))
                .thenReturn(emptyPage);

        // when
        Page<TodayReceiptResult> result = getTodayReceiptService.getTodayReceipt(today, pageable);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("특정 입고 계획을 ID로 조회한다")
    void getTodayReceiptById_WithValidId_ReturnsTodayReceipt() {
        // given
        when(getReceiptPlanPort.findTodayReceiptById(1L, today))
                .thenReturn(Optional.of(todayReceiptResult));

        // when
        TodayReceiptResult result = getTodayReceiptService.getTodayReceiptById(1L, today);

        // then
        assertThat(result).isNotNull();
        assertThat(result.receiptPlanId()).isEqualTo(todayReceiptResult.receiptPlanId());
        assertThat(result.receiptPlanCode()).isEqualTo(todayReceiptResult.receiptPlanCode());
        assertThat(result.receiptStatus()).isEqualTo("PENDING");
        assertThat(result.receiptCount()).isEqualTo(todayReceiptResult.receiptCount());
        assertThat(result.itemCount()).isEqualTo(todayReceiptResult.itemCount());
    }

    @Test
    @DisplayName("존재하지 않는 입고 계획 ID로 조회시 예외가 발생한다")
    void getTodayReceiptById_WithInvalidId_ThrowsException() {
        // given
        when(getReceiptPlanPort.findTodayReceiptById(999L, today))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> getTodayReceiptService.getTodayReceiptById(999L, today))
                .isInstanceOf(ReceiptNotFoundException.class);
    }

    @Test
    @DisplayName("실시간 입고 계획 현황을 구독한다")
    void subscribeTodayReceipts_ReturnsFlux() {
        // given
        Flux<ServerSentEvent<Object>> expectedFlux = Flux.just(
                ServerSentEvent.builder().data(todayReceiptResult).build()
        );

        when(getReceiptPlanPort.subscribeTodayReceipts(eq(today), eq(0), eq(10)))
                .thenReturn(expectedFlux);

        // when
        Flux<ServerSentEvent<Object>> result = getTodayReceiptService.subscribeTodayReceipts(today, 0, 10);

        // then
        StepVerifier.create(result)
                .assertNext(event -> {
                    assertThat(event.data()).isInstanceOf(TodayReceiptResult.class);
                    TodayReceiptResult data = (TodayReceiptResult) event.data();
                    assertThat(data.receiptPlanId()).isEqualTo(todayReceiptResult.receiptPlanId());
                    assertThat(data.receiptStatus()).isEqualTo(todayReceiptResult.receiptStatus());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("입고 계획 현황 업데이트를 알린다")
    void notifyReceiptUpdate_NotifiesSubscribers() {
        // when
        getTodayReceiptService.notifyReceiptUpdate(todayReceiptResult, today);

        // then
        verify(getReceiptPlanPort, times(1))
                .emitTodayReceiptUpdate(eq(todayReceiptResult), eq(today));
    }
}