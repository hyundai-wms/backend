package com.myme.mywarehome.domains.mrp.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecord;
import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecordItem;
import com.myme.mywarehome.domains.mrp.application.port.in.result.ProductStockCount;
import com.myme.mywarehome.domains.mrp.application.port.out.CreateInventoryRecordPort;
import com.myme.mywarehome.domains.mrp.application.port.out.LoadProductWithStocksForInventoryRecordPort;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateInventoryRecordServiceTest {

    @Mock
    private CreateInventoryRecordPort createInventoryRecordPort;

    @Mock
    private LoadProductWithStocksForInventoryRecordPort loadProductWithStocksForInventoryRecordPort;

    @Captor
    private ArgumentCaptor<InventoryRecord> inventoryRecordCaptor;

    @Captor
    private ArgumentCaptor<List<InventoryRecordItem>> inventoryRecordItemListCaptor;

    private CreateInventoryRecordService createInventoryRecordService;

    private List<ProductStockCount> mockProductStockCounts;

    @BeforeEach
    void setUp() {
        createInventoryRecordService = new CreateInventoryRecordService(
                createInventoryRecordPort,
                loadProductWithStocksForInventoryRecordPort
        );

        mockProductStockCounts = List.of(
                new ProductStockCount(
                        1L,
                        "00000-00P00",
                        "PRODUCT1",
                        1,
                        6,
                        7L
                ),
                new ProductStockCount(
                        2L,
                        "00000-01P00",
                        "PRODUCT2",
                        1,
                        3,
                        7L
                )
        );
    }

    @Test
    @DisplayName("재고 기록 생성 시 모든 제품의 재고 현황이 정상적으로 저장된다")
    void createInventoryRecord_withValidProducts_shouldSaveInventoryRecordSuccessfully() {
        // given
        when(loadProductWithStocksForInventoryRecordPort.loadAllProductsWithAvailableStocks())
                .thenReturn(mockProductStockCounts);

        // when
        createInventoryRecordService.createInventoryRecord();

        // then
        verify(createInventoryRecordPort).createInventoryRecord(
                inventoryRecordCaptor.capture(),
                inventoryRecordItemListCaptor.capture()
        );

        InventoryRecord capturedRecord = inventoryRecordCaptor.getValue();
        List<InventoryRecordItem> capturedItems = inventoryRecordItemListCaptor.getValue();

        assertThat(capturedRecord.getStockStatusAt()).isNotNull();
        assertThat(capturedItems).hasSize(2);

        // First product verification
        assertThat(capturedItems.get(0))
                .satisfies(item -> {
                    assertThat(item.getProduct().getProductId()).isEqualTo(1L);
                    assertThat(item.getCompositionRatio()).isEqualTo(1);
                    assertThat(item.getLeadTime()).isEqualTo(6);
                    assertThat(item.getStockCount()).isEqualTo(7L);
                    assertThat(item.getInventoryRecord()).isEqualTo(capturedRecord);
                });

        // Second product verification
        assertThat(capturedItems.get(1))
                .satisfies(item -> {
                    assertThat(item.getProduct().getProductId()).isEqualTo(2L);
                    assertThat(item.getStockCount()).isEqualTo(7L);
                    assertThat(item.getCompositionRatio()).isEqualTo(1);
                    assertThat(item.getLeadTime()).isEqualTo(3);
                    assertThat(item.getInventoryRecord()).isEqualTo(capturedRecord);
                });
    }
}