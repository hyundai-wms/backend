package com.myme.mywarehome.domains.stock.application.service;

import com.myme.mywarehome.domains.stock.application.port.in.result.BayWithStockBinResult;
import com.myme.mywarehome.domains.stock.application.port.in.result.BinInfoResult;
import com.myme.mywarehome.domains.stock.application.port.out.GetBayPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetBayServiceTest {

    @Mock
    private GetBayPort getBayPort;

    private GetBayService getBayService;
    private BinInfoResult binInfoResult;
    private BayWithStockBinResult bayWithStockBinResult;

    @BeforeEach
    void setUp() {
        getBayService = new GetBayService(getBayPort);

        binInfoResult = BinInfoResult.builder()
                .bayNumber("BAY001")
                .binLocation(1)
                .build();

        bayWithStockBinResult = BayWithStockBinResult.builder()
                .bayNumber("BAY001")
                .productNumber("PROD001")
                .build();
    }

    @Test
    @DisplayName("베이 번호로 빈 정보를 조회할 수 있다")
    void getBayByBayNumber_WhenValidBayNumber_ThenReturnBinInfoList() {
        // given
        String bayNumber = "BAY001";
        List<BinInfoResult> expectedResults = List.of(binInfoResult);
        when(getBayPort.getBayByBayNumber(bayNumber)).thenReturn(expectedResults);

        // when
        List<BinInfoResult> results = getBayService.getBayByBayNumber(bayNumber);

        // then
        assertThat(results).isNotNull();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).bayNumber()).isEqualTo("BAY001");
        assertThat(results.get(0).binLocation()).isEqualTo(1);
        verify(getBayPort, times(1)).getBayByBayNumber(bayNumber);
    }

    @Test
    @DisplayName("품번으로 베이 리스트를 조회할 수 있다")
    void getBayListByProductNumber_WhenValidProductNumber_ThenReturnBayList() {
        // given
        String productNumber = "PROD001";
        List<BayWithStockBinResult> expectedResults = List.of(bayWithStockBinResult);
        when(getBayPort.getAllBayByProductNumber(productNumber)).thenReturn(expectedResults);

        // when
        List<BayWithStockBinResult> results = getBayService.getBayListByProductNumber(productNumber);

        // then
        assertThat(results).isNotNull();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).bayNumber()).isEqualTo("BAY001");
        assertThat(results.get(0).productNumber()).isEqualTo("PROD001");
        verify(getBayPort, times(1)).getAllBayByProductNumber(productNumber);
    }

    @Test
    @DisplayName("베이 변동을 구독할 수 있다")
    void subscribeBayFluctuation_WhenSubscribed_ThenReceiveEvents() {
        // given
        ServerSentEvent<Object> event = ServerSentEvent.builder()
                .data(bayWithStockBinResult)
                .build();
        Flux<ServerSentEvent<Object>> expectedFlux = Flux.just(event);
        when(getBayPort.subscribeBayFluctuation()).thenReturn(expectedFlux);

        // when
        Flux<ServerSentEvent<Object>> result = getBayService.subscribeBayFluctuation();

        // then
        StepVerifier.create(result)
                .expectNextMatches(sentEvent ->
                        sentEvent.data().equals(bayWithStockBinResult))
                .verifyComplete();
        verify(getBayPort, times(1)).subscribeBayFluctuation();
    }

    @Test
    @DisplayName("베이 업데이트 알림을 발행할 수 있다")
    void notifyBayUpdate_WhenCalled_ThenEmitUpdate() {
        // when
        getBayService.notifyBayUpdate(bayWithStockBinResult);

        // then
        verify(getBayPort, times(1)).emitBayUpdate(bayWithStockBinResult);
    }
}