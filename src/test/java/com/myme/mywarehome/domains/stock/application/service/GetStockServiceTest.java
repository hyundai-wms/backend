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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetStockServiceTest {

    @Mock
    private GetStockPort getStockPort;

    private GetStockService getStockService;
    private Stock stock;
    private Pageable pageable;
    private LocalDate selectedDate;

    @BeforeEach
    void setUp() {
        getStockService = new GetStockService(getStockPort);

        stock = Stock.builder()
                .stockId(1L)
                .stockEventType(StockEventType.RECEIPT)
                .build();

        pageable = PageRequest.of(0, 10);
        selectedDate = LocalDate.now();
    }

    @Test
    @DisplayName("품번으로 재고 목록을 페이지네이션하여 조회할 수 있다")
    void getStockList_WhenValidProductNumber_ThenReturnPagedStockList() {
        // given
        String productNumber = "PROD001";
        Page<Stock> expectedPage = new PageImpl<>(
                List.of(stock),
                pageable,
                1
        );
        when(getStockPort.findByProductNumber(productNumber, pageable, selectedDate))
                .thenReturn(expectedPage);

        // when
        Page<Stock> result = getStockService.getStockList(productNumber, pageable, selectedDate);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStockId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).getLastEventType()).isEqualTo(StockEventType.RECEIPT);
        verify(getStockPort, times(1)).findByProductNumber(productNumber, pageable, selectedDate);
    }

    @Test
    @DisplayName("존재하지 않는 품번으로 조회시 빈 페이지를 반환한다")
    void getStockList_WhenProductNotExists_ThenReturnEmptyPage() {
        // given
        String nonExistentProductNumber = "NONEXISTENT";
        Page<Stock> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(getStockPort.findByProductNumber(nonExistentProductNumber, pageable, selectedDate))
                .thenReturn(emptyPage);

        // when
        Page<Stock> result = getStockService.getStockList(nonExistentProductNumber, pageable, selectedDate);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        verify(getStockPort, times(1)).findByProductNumber(nonExistentProductNumber, pageable, selectedDate);
    }
}