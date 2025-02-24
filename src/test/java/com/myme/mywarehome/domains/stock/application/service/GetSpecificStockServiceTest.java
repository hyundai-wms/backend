package com.myme.mywarehome.domains.stock.application.service;

import com.myme.mywarehome.domains.stock.application.domain.Stock;
import com.myme.mywarehome.domains.stock.application.domain.StockEventType;
import com.myme.mywarehome.domains.stock.application.port.out.GetStockPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSpecificStockServiceTest {

    @Mock
    private GetStockPort getStockPort;

    private GetSpecificStockService getSpecificStockService;
    private Stock stock;

    @BeforeEach
    void setUp() {
        getSpecificStockService = new GetSpecificStockService(getStockPort);

        stock = Stock.builder()
                .stockId(1L)
                .stockEventType(StockEventType.RECEIPT)
                .build();
    }

    @Test
    @DisplayName("재고 ID로 특정 재고를 조회할 수 있다")
    void getSpecificStock_WhenStockExists_ThenReturnStock() {
        // given
        Long stockId = 1L;
        when(getStockPort.findById(stockId)).thenReturn(Optional.of(stock));

        // when
        Optional<Stock> result = getSpecificStockService.getSpecificStock(stockId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getStockId()).isEqualTo(stockId);
        assertThat(result.get().getLastEventType()).isEqualTo(StockEventType.RECEIPT);
        verify(getStockPort, times(1)).findById(stockId);
    }

    @Test
    @DisplayName("존재하지 않는 재고 ID로 조회시 빈 Optional을 반환한다")
    void getSpecificStock_WhenStockNotExists_ThenReturnEmpty() {
        // given
        Long nonExistentStockId = 999L;
        when(getStockPort.findById(nonExistentStockId)).thenReturn(Optional.empty());

        // when
        Optional<Stock> result = getSpecificStockService.getSpecificStock(nonExistentStockId);

        // then
        assertThat(result).isEmpty();
        verify(getStockPort, times(1)).findById(nonExistentStockId);
    }
}