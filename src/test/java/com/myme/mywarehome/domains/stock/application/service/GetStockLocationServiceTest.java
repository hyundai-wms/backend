package com.myme.mywarehome.domains.stock.application.service;

import com.myme.mywarehome.domains.stock.application.port.in.result.BayWithStockBinResult;
import com.myme.mywarehome.domains.stock.application.port.out.GetBayPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetStockLocationServiceTest {

    @Mock
    private GetBayPort getBayPort;

    private GetStockLocationService getStockLocationService;
    private BayWithStockBinResult bayWithStockBinResult;

    @BeforeEach
    void setUp() {
        getStockLocationService = new GetStockLocationService(getBayPort);

        bayWithStockBinResult = BayWithStockBinResult.builder()
                .bayNumber("BAY001")
                .productNumber("PROD001")
                .stockCount(100L)
                .build();
    }

    @Test
    @DisplayName("품번으로 베이 위치 목록을 조회할 수 있다")
    void getBayList_WhenValidProductNumber_ThenReturnBayList() {
        // given
        String productNumber = "PROD001";
        List<BayWithStockBinResult> expectedResults = List.of(bayWithStockBinResult);
        when(getBayPort.getAllBayByProductNumber(productNumber)).thenReturn(expectedResults);

        // when
        List<BayWithStockBinResult> results = getStockLocationService.getBayList(productNumber);

        // then
        assertThat(results).isNotNull();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).bayNumber()).isEqualTo("BAY001");
        assertThat(results.get(0).productNumber()).isEqualTo("PROD001");
        assertThat(results.get(0).stockCount()).isEqualTo(100L);
        verify(getBayPort, times(1)).getAllBayByProductNumber(productNumber);
    }

    @Test
    @DisplayName("존재하지 않는 품번으로 조회시 빈 목록을 반환한다")
    void getBayList_WhenProductNotExists_ThenReturnEmptyList() {
        // given
        String nonExistentProductNumber = "NONEXISTENT";
        when(getBayPort.getAllBayByProductNumber(nonExistentProductNumber)).thenReturn(List.of());

        // when
        List<BayWithStockBinResult> results = getStockLocationService.getBayList(nonExistentProductNumber);

        // then
        assertThat(results).isNotNull();
        assertThat(results).isEmpty();
        verify(getBayPort, times(1)).getAllBayByProductNumber(nonExistentProductNumber);
    }
}