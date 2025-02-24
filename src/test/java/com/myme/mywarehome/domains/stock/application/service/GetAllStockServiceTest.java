package com.myme.mywarehome.domains.stock.application.service;

import com.myme.mywarehome.domains.stock.application.port.in.command.StockSummaryCommand;
import com.myme.mywarehome.domains.stock.application.port.in.result.StockSummaryResult;
import com.myme.mywarehome.domains.stock.application.port.out.GetStockPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllStockServiceTest {

    @Mock
    private GetStockPort getStockPort;

    private GetAllStockService getAllStockService;
    private StockSummaryCommand command;
    private Pageable pageable;
    private LocalDate selectedDate;
    private StockSummaryResult stockSummaryResult;

    @BeforeEach
    void setUp() {
        getAllStockService = new GetAllStockService(getStockPort);
        command = StockSummaryCommand.builder().build();
        pageable = PageRequest.of(0, 10);
        selectedDate = LocalDate.now();
        stockSummaryResult = StockSummaryResult.builder()
                .productNumber("TEST001")
                .build();
    }

    @Test
    @DisplayName("재고 목록을 페이지네이션하여 조회할 수 있다")
    void getAllStockList_WhenValidRequest_ThenReturnPagedResult() {
        // given
        Page<StockSummaryResult> expectedPage = new PageImpl<>(
                List.of(stockSummaryResult),
                pageable,
                1
        );
        when(getStockPort.findStockSummaries(any(), any(), any())).thenReturn(expectedPage);

        // when
        Page<StockSummaryResult> result = getAllStockService.getAllStockList(command, pageable, selectedDate);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).productNumber()).isEqualTo("TEST001");
        verify(getStockPort, times(1)).findStockSummaries(command, pageable, selectedDate);
    }

    @Test
    @DisplayName("품번으로 재고를 조회할 수 있다")
    void getStockByProductNumber_WhenProductExists_ThenReturnStock() {
        // given
        String productNumber = "TEST001";
        when(getStockPort.findStockSummaryByProductNumber(productNumber))
                .thenReturn(Optional.of(stockSummaryResult));

        // when
        StockSummaryResult result = getAllStockService.getStockByProductNumber(productNumber);

        // then
        assertThat(result).isNotNull();
        assertThat(result.productNumber()).isEqualTo("TEST001");
        verify(getStockPort, times(1)).findStockSummaryByProductNumber(productNumber);
    }

    @Test
    @DisplayName("존재하지 않는 품번으로 조회시 빈 결과를 반환한다")
    void getStockByProductNumber_WhenProductNotExists_ThenReturnEmptyResult() {
        // given
        String productNumber = "NONEXISTENT";
        when(getStockPort.findStockSummaryByProductNumber(productNumber))
                .thenReturn(Optional.empty());

        // when
        StockSummaryResult result = getAllStockService.getStockByProductNumber(productNumber);

        // then
        assertThat(result).isNotNull();
        assertThat(result.productNumber()).isNull();
        verify(getStockPort, times(1)).findStockSummaryByProductNumber(productNumber);
    }

    @Test
    @DisplayName("재고 변동을 구독할 수 있다")
    void subscribeStockFluctuation_WhenSubscribed_ThenReceiveEvents() {
        // given
        ServerSentEvent<Object> event = ServerSentEvent.builder()
                .data(stockSummaryResult)
                .build();
        Flux<ServerSentEvent<Object>> expectedFlux = Flux.just(event);
        when(getStockPort.subscribeStockFluctuation(any(), any(), any())).thenReturn(expectedFlux);

        // when
        Flux<ServerSentEvent<Object>> result = getAllStockService.subscribeStockFluctuation(
                command, pageable, selectedDate);

        // then
        StepVerifier.create(result)
                .expectNextMatches(sentEvent ->
                        sentEvent.data().equals(stockSummaryResult))
                .verifyComplete();
        verify(getStockPort, times(1)).subscribeStockFluctuation(command, pageable, selectedDate);
    }

    @Test
    @DisplayName("재고 업데이트 알림을 발행할 수 있다")
    void notifyStockUpdate_WhenCalled_ThenEmitUpdate() {
        // when
        getAllStockService.notifyStockUpdate(stockSummaryResult);

        // then
        verify(getStockPort, times(1)).emitStockUpdate(stockSummaryResult);
    }
}