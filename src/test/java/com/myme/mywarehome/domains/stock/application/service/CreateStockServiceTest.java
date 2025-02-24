package com.myme.mywarehome.domains.stock.application.service;

import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.receipt.application.domain.Receipt;
import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.stock.application.domain.Bin;
import com.myme.mywarehome.domains.stock.application.domain.Stock;
import com.myme.mywarehome.domains.stock.application.domain.StockEventType;
import com.myme.mywarehome.domains.stock.application.exception.NoAvailableBinException;
import com.myme.mywarehome.domains.stock.application.port.out.CreateStockPort;
import com.myme.mywarehome.domains.stock.application.port.out.GetBinPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateStockServiceTest {

    @Mock
    private GetBinPort getBinPort;

    @Mock
    private CreateStockPort createStockPort;

    private CreateStockService createStockService;

    private Receipt receipt;
    private Product product;
    private Bin bin;
    private Stock stock;

    @BeforeEach
    void setUp() {
        createStockService = new CreateStockService(getBinPort, createStockPort);

        product = Product.builder()
                .productNumber("TEST001")
                .build();

        ReceiptPlan receiptPlan = ReceiptPlan.builder()
                .product(product)
                .build();

        receipt = Receipt.builder()
                .receiptPlan(receiptPlan)
                .build();

        bin = Bin.builder()
                .binLocation(1)
                .build();

        stock = Stock.builder()
                .stockEventType(StockEventType.RECEIPT)
                .receipt(receipt)
                .build();
    }

    @Test
    @DisplayName("단일 재고 생성 시 성공적으로 생성되어야 한다")
    void createStock_WhenValidReceipt_ThenSuccess() {
        // given
        when(getBinPort.findBinByProductNumber(any())).thenReturn(Optional.of(bin));
        when(createStockPort.create(any())).thenReturn(stock);

        // when
        Stock result = createStockService.createStock(receipt);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getLastEventType()).isEqualTo(StockEventType.RECEIPT);
        assertThat(result.getReceipt()).isEqualTo(receipt);
        assertThat(result.getBin()).isEqualTo(bin);

        verify(createStockPort, times(1)).create(any());
        verify(getBinPort, times(1)).findBinByProductNumber(any());
    }

    @Test
    @DisplayName("사용 가능한 빈이 없을 경우 예외가 발생해야 한다")
    void createStock_WhenNoBinAvailable_ThenThrowException() {
        // given
        when(getBinPort.findBinByProductNumber(any())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> createStockService.createStock(receipt))
                .isInstanceOf(NoAvailableBinException.class);
    }

    @Test
    @DisplayName("벌크 재고 생성 시 성공적으로 생성되어야 한다")
    void createStockBulk_WhenValidReceipts_ThenSuccess() {
        // given
        List<Receipt> receiptList = new ArrayList<>(List.of(receipt));
        Map<String, List<Bin>> binMap = new HashMap<>();
        binMap.put(product.getProductNumber(), new ArrayList<>(List.of(bin)));

        when(getBinPort.findAvailableBinsByProductNumbers(any())).thenReturn(binMap);

        // when
        createStockService.createStockBulk(receiptList);

        // then
        verify(createStockPort, times(1)).createBulk(any());
        verify(getBinPort, times(1)).findAvailableBinsByProductNumbers(any());
    }

    @Test
    @DisplayName("벌크 생성 시 사용 가능한 빈이 없을 경우 예외가 발생해야 한다")
    void createStockBulk_WhenNoBinAvailable_ThenThrowException() {
        // given
        List<Receipt> receiptList = Collections.singletonList(receipt);
        when(getBinPort.findAvailableBinsByProductNumbers(any())).thenReturn(new HashMap<>());

        // when & then
        assertThatThrownBy(() -> createStockService.createStockBulk(receiptList))
                .isInstanceOf(NoAvailableBinException.class);
    }
}