package com.myme.mywarehome.domains.issue.application.service;

import com.myme.mywarehome.domains.issue.adapter.out.exception.IssuePlanItemCountExceededException;
import com.myme.mywarehome.domains.issue.adapter.out.exception.IssuePlanNotFoundException;
import com.myme.mywarehome.domains.issue.adapter.out.exception.StockAlreadyIssuedException;
import com.myme.mywarehome.domains.issue.adapter.out.exception.StockAssignTimeoutException;
import com.myme.mywarehome.domains.issue.application.domain.Issue;
import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.in.command.IssueProcessCommand;
import com.myme.mywarehome.domains.issue.application.port.in.event.IssuePlanStatusChangedEvent;
import com.myme.mywarehome.domains.issue.application.port.in.event.IssueStockAssignEvent;
import com.myme.mywarehome.domains.issue.application.port.out.CreateIssuePort;
import com.myme.mywarehome.domains.issue.application.port.out.GetIssuePlanPort;
import com.myme.mywarehome.domains.issue.application.port.out.GetIssuePort;
import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.stock.adapter.in.event.event.BayUpdateEvent;
import com.myme.mywarehome.domains.stock.adapter.in.event.event.StockUpdateEvent;
import com.myme.mywarehome.domains.stock.application.domain.Stock;
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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssueProcessServiceTest {

    @InjectMocks
    private IssueProcessService issueProcessService;

    @Mock
    private GetIssuePort getIssuePort;

    @Mock
    private GetIssuePlanPort getIssuePlanPort;

    @Mock
    private CreateIssuePort createIssuePort;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Captor
    private ArgumentCaptor<IssueStockAssignEvent> stockAssignEventCaptor;

    @Captor
    private ArgumentCaptor<IssuePlanStatusChangedEvent> statusChangedEventCaptor;

    @Captor
    private ArgumentCaptor<StockUpdateEvent> stockUpdateEventCaptor;

    @Captor
    private ArgumentCaptor<BayUpdateEvent> bayUpdateEventCaptor;

    private IssuePlan issuePlan;
    private Issue issue;
    private Product product;
    private Stock stock;
    private LocalDate today;
    private Long stockId;
    private IssueProcessCommand command;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        stockId = 1L;

        product = Product.builder()
                .productNumber("TEST001")
                .productName("테스트 상품")
                .build();

        issuePlan = IssuePlan.builder()
                .issuePlanId(1L)
                .issuePlanCode("IP001")
                .issuePlanDate(today)
                .issuePlanItemCount(10)
                .product(product)
                .build();

        issue = Issue.builder()
                .issueId(1L)
                .issuePlan(issuePlan)
                .product(product)
                .issueDate(today)
                .build();

        stock = Stock.builder()
                .stockId(stockId)
                .build();

        command = new IssueProcessCommand(issuePlan.getIssuePlanId());
    }

    @Test
    @DisplayName("출고 처리를 정상적으로 수행한다")
    void process_WithValidData_ProcessesIssueSuccessfully() {
        // given
        when(getIssuePort.existsByStockId(stockId)).thenReturn(false);
        when(getIssuePlanPort.getIssuePlanById(command.issuePlanId())).thenReturn(Optional.of(issuePlan));
        when(getIssuePort.countProcessedIssueByIssuePlanId(issuePlan.getIssuePlanId())).thenReturn(0L);
        when(createIssuePort.create(any(Issue.class))).thenReturn(issue);

        // CompletableFuture 설정
        doAnswer(invocation -> {
            IssueStockAssignEvent event = invocation.getArgument(0);
            event.future().complete(stock);
            return null;
        }).when(eventPublisher).publishEvent(any(IssueStockAssignEvent.class));

        // when
        Issue result = issueProcessService.process(stockId, command, today);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getIssuePlan()).isEqualTo(issuePlan);
        assertThat(result.getProduct()).isEqualTo(product);

        // 이벤트 발행 검증
        verify(eventPublisher).publishEvent(stockAssignEventCaptor.capture());
        verify(eventPublisher).publishEvent(statusChangedEventCaptor.capture());
        verify(eventPublisher).publishEvent(stockUpdateEventCaptor.capture());
        verify(eventPublisher).publishEvent(bayUpdateEventCaptor.capture());

        // 이벤트 내용 검증
        IssueStockAssignEvent stockAssignEvent = stockAssignEventCaptor.getValue();
        assertThat(stockAssignEvent.issue()).isEqualTo(issue);
        assertThat(stockAssignEvent.stockId()).isEqualTo(stockId);

        IssuePlanStatusChangedEvent statusChangedEvent = statusChangedEventCaptor.getValue();
        assertThat(statusChangedEvent.issuePlanId()).isEqualTo(issuePlan.getIssuePlanId());
        assertThat(statusChangedEvent.eventType()).isEqualTo("ISSUE_PROCESSED");

        StockUpdateEvent stockUpdateEvent = stockUpdateEventCaptor.getValue();
        assertThat(stockUpdateEvent.productNumber()).isEqualTo(product.getProductNumber());

        BayUpdateEvent bayUpdateEvent = bayUpdateEventCaptor.getValue();
        assertThat(bayUpdateEvent.productNumber()).isEqualTo(product.getProductNumber());
    }

    @Test
    @DisplayName("이미 출고된 재고를 처리하려 할 때 예외가 발생한다")
    void process_WithAlreadyIssuedStock_ThrowsException() {
        // given
        when(getIssuePort.existsByStockId(stockId)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> issueProcessService.process(stockId, command, today))
                .isInstanceOf(StockAlreadyIssuedException.class);

        verify(getIssuePlanPort, never()).getIssuePlanById(any());
        verify(createIssuePort, never()).create(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("존재하지 않는 출고 계획으로 처리시 예외가 발생한다")
    void process_WithNonExistentIssuePlan_ThrowsException() {
        // given
        when(getIssuePort.existsByStockId(stockId)).thenReturn(false);
        when(getIssuePlanPort.getIssuePlanById(command.issuePlanId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> issueProcessService.process(stockId, command, today))
                .isInstanceOf(IssuePlanNotFoundException.class);

        verify(createIssuePort, never()).create(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("출고 계획 수량을 초과하여 처리시 예외가 발생한다")
    void process_WithExceededItemCount_ThrowsException() {
        // given
        when(getIssuePort.existsByStockId(stockId)).thenReturn(false);
        when(getIssuePlanPort.getIssuePlanById(command.issuePlanId())).thenReturn(Optional.of(issuePlan));
        when(getIssuePort.countProcessedIssueByIssuePlanId(issuePlan.getIssuePlanId()))
                .thenReturn(Long.valueOf(issuePlan.getIssuePlanItemCount()));

        // when & then
        assertThatThrownBy(() -> issueProcessService.process(stockId, command, today))
                .isInstanceOf(IssuePlanItemCountExceededException.class);

        verify(createIssuePort, never()).create(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    @DisplayName("재고 할당 시간 초과시 예외가 발생한다")
    void process_WithStockAssignTimeout_ThrowsException() {
        // given
        when(getIssuePort.existsByStockId(stockId)).thenReturn(false);
        when(getIssuePlanPort.getIssuePlanById(command.issuePlanId())).thenReturn(Optional.of(issuePlan));
        when(getIssuePort.countProcessedIssueByIssuePlanId(issuePlan.getIssuePlanId())).thenReturn(0L);
        when(createIssuePort.create(any(Issue.class))).thenReturn(issue);

        // CompletableFuture 타임아웃 시뮬레이션
        doAnswer(invocation -> {
            IssueStockAssignEvent event = invocation.getArgument(0);
            CompletableFuture<Stock> future = event.future();
            future.completeExceptionally(new Exception("Timeout"));
            return null;
        }).when(eventPublisher).publishEvent(any(IssueStockAssignEvent.class));

        // when & then
        assertThatThrownBy(() -> issueProcessService.process(stockId, command, today))
                .isInstanceOf(StockAssignTimeoutException.class);
    }
}