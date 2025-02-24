package com.myme.mywarehome.domains.mrp.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecord;
import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecordItem;
import com.myme.mywarehome.domains.mrp.application.port.in.command.GetInventoryRecordItemCommand;
import com.myme.mywarehome.domains.mrp.application.port.out.GetInventoryRecordItemPort;
import com.myme.mywarehome.domains.product.application.domain.Product;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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

@ExtendWith(MockitoExtension.class)
class GetInventoryRecordItemServiceTest {

    @Mock
    private GetInventoryRecordItemPort getInventoryRecordItemPort;

    private GetInventoryRecordItemService getInventoryRecordItemService;

    private List<InventoryRecordItem> mockInventoryRecordItems;
    private GetInventoryRecordItemCommand command;
    private Pageable pageable;
    private Product product;
    private InventoryRecord inventoryRecord;

    @BeforeEach
    void setUp() {
        getInventoryRecordItemService = new GetInventoryRecordItemService(getInventoryRecordItemPort);

        // Mock 데이터 설정
        product = Product.builder()
                .productId(1L)
                .productName("테스트 제품")
                .build();

        inventoryRecord = InventoryRecord.builder()
                .stockStatusAt(LocalDateTime.now())
                .build();

        mockInventoryRecordItems = List.of(
                InventoryRecordItem.builder()
                        .inventoryRecord(inventoryRecord)
                        .product(product)
                        .stockCount(100L)
                        .compositionRatio(1)
                        .leadTime(7)
                        .build(),
                InventoryRecordItem.builder()
                        .inventoryRecord(inventoryRecord)
                        .product(product)
                        .stockCount(50L)
                        .compositionRatio(1)
                        .leadTime(5)
                        .build()
        );

        command = new GetInventoryRecordItemCommand(1L, "00000-00P00", "테스트 제품");
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("재고 기록 아이템 목록을 페이지네이션하여 조회한다")
    void getInventoryRecordItem_withPagination_shouldReturnPagedResult() {
        // given
        Page<InventoryRecordItem> expectedPage = new PageImpl<>(
                mockInventoryRecordItems,
                pageable,
                mockInventoryRecordItems.size()
        );

        when(getInventoryRecordItemPort.findInventoryRecordItems(command, pageable))
                .thenReturn(expectedPage);

        // when
        Page<InventoryRecordItem> result = getInventoryRecordItemService
                .getInventoryRecordItem(command, pageable);

        // then
        verify(getInventoryRecordItemPort).findInventoryRecordItems(command, pageable);

        assertThat(result.getContent())
                .hasSize(2)
                .usingRecursiveComparison()
                .isEqualTo(mockInventoryRecordItems);

        assertThat(result.getNumber()).isZero();
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isEqualTo(2);

        // 첫 번째 아이템 검증
        assertThat(result.getContent().get(0))
                .satisfies(item -> {
                    assertThat(item.getProduct().getProductId()).isEqualTo(1L);
                    assertThat(item.getProduct().getProductName()).isEqualTo("테스트 제품");
                    assertThat(item.getStockCount()).isEqualTo(100L);
                    assertThat(item.getCompositionRatio()).isEqualTo(1);
                    assertThat(item.getLeadTime()).isEqualTo(7);
                    assertThat(item.getInventoryRecord()).isEqualTo(inventoryRecord);
                });

        // 두 번째 아이템 검증
        assertThat(result.getContent().get(1))
                .satisfies(item -> {
                    assertThat(item.getProduct().getProductId()).isEqualTo(1L);
                    assertThat(item.getProduct().getProductName()).isEqualTo("테스트 제품");
                    assertThat(item.getStockCount()).isEqualTo(50L);
                    assertThat(item.getCompositionRatio()).isEqualTo(1);
                    assertThat(item.getLeadTime()).isEqualTo(5);
                    assertThat(item.getInventoryRecord()).isEqualTo(inventoryRecord);
                });
    }

    @Test
    @DisplayName("빈 결과를 조회할 경우 빈 페이지를 반환한다")
    void getInventoryRecordItem_whenEmpty_shouldReturnEmptyPage() {
        // given
        Page<InventoryRecordItem> emptyPage = new PageImpl<>(
                List.of(),
                pageable,
                0
        );

        when(getInventoryRecordItemPort.findInventoryRecordItems(command, pageable))
                .thenReturn(emptyPage);

        // when
        Page<InventoryRecordItem> result = getInventoryRecordItemService
                .getInventoryRecordItem(command, pageable);

        // then
        verify(getInventoryRecordItemPort).findInventoryRecordItems(command, pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }
}