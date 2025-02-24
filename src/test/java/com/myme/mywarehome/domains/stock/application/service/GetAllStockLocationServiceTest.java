package com.myme.mywarehome.domains.stock.application.service;

import com.myme.mywarehome.domains.stock.application.port.in.result.BayWithStockBinResult;
import com.myme.mywarehome.domains.stock.application.port.out.GetBayPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GetAllStockLocationServiceTest {

    private GetAllStockLocationService getAllStockLocationService;

    @Mock
    private GetBayPort getBayPort;

    @BeforeEach
    void setUp() {
        getAllStockLocationService = new GetAllStockLocationService(getBayPort);
    }

    @Test
    @DisplayName("모든 베이 목록을 페이징하여 정상적으로 조회한다")
    void getAllBayList_withPageable_returnsPaginatedBayList() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<BayWithStockBinResult> mockPage = mock(Page.class);

        // when
        when(getBayPort.getAllBayList(pageable)).thenReturn(mockPage);
        Page<BayWithStockBinResult> result = getAllStockLocationService.getAllBayList(pageable);

        // then
        assertThat(result).isEqualTo(mockPage);
        verify(getBayPort).getAllBayList(pageable);
    }

    @Test
    @DisplayName("베이 변동 사항을 구독할 때 Flux 이벤트 스트림을 반환한다")
    void subscribeBayFluctuation_returnsFluxOfServerSentEvents() {
        // given
        Flux<ServerSentEvent<Object>> mockFlux = mock(Flux.class);

        // when
        when(getBayPort.subscribeBayFluctuation()).thenReturn(mockFlux);
        Flux<ServerSentEvent<Object>> result = getAllStockLocationService.subscribeBayFluctuation();

        // then
        verify(getBayPort).subscribeBayFluctuation();
        assertThat(result).isEqualTo(mockFlux);
    }

    @Test
    @DisplayName("베이 업데이트 알림을 정상적으로 전송한다")
    void notifyBayUpdate_sendsUpdateEvent() {
        // given
        BayWithStockBinResult bayUpdate = mock(BayWithStockBinResult.class);

        // when
        getAllStockLocationService.notifyBayUpdate(bayUpdate);

        // then
        verify(getBayPort).emitBayUpdate(bayUpdate);
    }
}