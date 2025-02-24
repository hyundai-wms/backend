package com.myme.mywarehome.domains.receipt.application.service;

import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.product.application.exception.ProductNotFoundException;
import com.myme.mywarehome.domains.product.application.port.out.GetProductPort;
import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.exception.ReceiptPlanNotFoundException;
import com.myme.mywarehome.domains.receipt.application.port.in.command.ReceiptPlanCommand;
import com.myme.mywarehome.domains.receipt.application.port.out.GetReceiptPlanPort;
import com.myme.mywarehome.domains.receipt.application.port.out.UpdateReceiptPlanPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateReceiptPlanServiceTest {

    @InjectMocks
    private UpdateReceiptPlanService updateReceiptPlanService;

    @Mock
    private GetProductPort getProductPort;

    @Mock
    private GetReceiptPlanPort getReceiptPlanPort;

    @Mock
    private UpdateReceiptPlanPort updateReceiptPlanPort;

    private Product originalProduct;
    private Product newProduct;
    private ReceiptPlan receiptPlan;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();

        originalProduct = Product.builder()
                .productNumber("OLD001")
                .productName("기존 상품")
                .build();

        newProduct = Product.builder()
                .productNumber("NEW001")
                .productName("새로운 상품")
                .build();

        receiptPlan = ReceiptPlan.builder()
                .receiptPlanId(1L)
                .receiptPlanCode("RP001")
                .receiptPlanItemCount(10)
                .receiptPlanDate(today)
                .product(originalProduct)
                .build();
    }

    @Test
    @DisplayName("입고 계획의 상품 정보를 수정한다")
    void updateReceiptPlan_WithNewProduct_UpdatesProductSuccessfully() {
        // given
        ReceiptPlanCommand command = new ReceiptPlanCommand(
                newProduct.getProductNumber(),
                null,
                null
        );

        when(getReceiptPlanPort.findReceiptPlanById(1L)).thenReturn(Optional.of(receiptPlan));
        when(getProductPort.findByProductNumber(command.productNumber())).thenReturn(Optional.of(newProduct));
        when(updateReceiptPlanPort.updateReceiptPlan(any(ReceiptPlan.class))).thenReturn(Optional.of(receiptPlan));

        // when
        ReceiptPlan updatedReceiptPlan = updateReceiptPlanService.updateReceiptPlan(1L, command);

        // then
        assertThat(updatedReceiptPlan.getProduct().getProductNumber()).isEqualTo(newProduct.getProductNumber());
    }

    @Test
    @DisplayName("입고 계획의 수량을 수정한다")
    void updateReceiptPlan_WithNewItemCount_UpdatesItemCountSuccessfully() {
        // given
        Integer newItemCount = 20;
        ReceiptPlanCommand command = new ReceiptPlanCommand(
                null,
                newItemCount,
                null
        );

        when(getReceiptPlanPort.findReceiptPlanById(1L)).thenReturn(Optional.of(receiptPlan));
        when(updateReceiptPlanPort.updateReceiptPlan(any(ReceiptPlan.class))).thenReturn(Optional.of(receiptPlan));

        // when
        ReceiptPlan updatedReceiptPlan = updateReceiptPlanService.updateReceiptPlan(1L, command);

        // then
        assertThat(updatedReceiptPlan.getReceiptPlanItemCount()).isEqualTo(newItemCount);
    }

    @Test
    @DisplayName("입고 계획의 날짜를 수정한다")
    void updateReceiptPlan_WithNewDate_UpdatesDateSuccessfully() {
        // given
        LocalDate newDate = today.plusDays(1);
        ReceiptPlanCommand command = new ReceiptPlanCommand(
                null,
                null,
                newDate
        );

        when(getReceiptPlanPort.findReceiptPlanById(1L)).thenReturn(Optional.of(receiptPlan));
        when(updateReceiptPlanPort.updateReceiptPlan(any(ReceiptPlan.class))).thenReturn(Optional.of(receiptPlan));

        // when
        ReceiptPlan updatedReceiptPlan = updateReceiptPlanService.updateReceiptPlan(1L, command);

        // then
        assertThat(updatedReceiptPlan.getReceiptPlanDate()).isEqualTo(newDate);
    }

    @Test
    @DisplayName("모든 필드를 한번에 수정한다")
    void updateReceiptPlan_WithAllFields_UpdatesAllFieldsSuccessfully() {
        // given
        Integer newItemCount = 20;
        LocalDate newDate = today.plusDays(1);
        ReceiptPlanCommand command = new ReceiptPlanCommand(
                newProduct.getProductNumber(),
                newItemCount,
                newDate
        );

        when(getReceiptPlanPort.findReceiptPlanById(1L)).thenReturn(Optional.of(receiptPlan));
        when(getProductPort.findByProductNumber(command.productNumber())).thenReturn(Optional.of(newProduct));
        when(updateReceiptPlanPort.updateReceiptPlan(any(ReceiptPlan.class))).thenReturn(Optional.of(receiptPlan));

        // when
        ReceiptPlan updatedReceiptPlan = updateReceiptPlanService.updateReceiptPlan(1L, command);

        // then
        assertThat(updatedReceiptPlan.getProduct().getProductNumber()).isEqualTo(newProduct.getProductNumber());
        assertThat(updatedReceiptPlan.getReceiptPlanItemCount()).isEqualTo(newItemCount);
        assertThat(updatedReceiptPlan.getReceiptPlanDate()).isEqualTo(newDate);
    }

    @Test
    @DisplayName("존재하지 않는 입고 계획 수정 시 예외가 발생한다")
    void updateReceiptPlan_WithNonExistentReceiptPlan_ThrowsException() {
        // given
        ReceiptPlanCommand command = new ReceiptPlanCommand(
                null,
                20,
                null
        );

        when(getReceiptPlanPort.findReceiptPlanById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> updateReceiptPlanService.updateReceiptPlan(999L, command))
                .isInstanceOf(ReceiptPlanNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 상품으로 수정 시 예외가 발생한다")
    void updateReceiptPlan_WithNonExistentProduct_ThrowsException() {
        // given
        ReceiptPlanCommand command = new ReceiptPlanCommand(
                "NON_EXISTENT",
                null,
                null
        );

        when(getReceiptPlanPort.findReceiptPlanById(1L)).thenReturn(Optional.of(receiptPlan));
        when(getProductPort.findByProductNumber(command.productNumber())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> updateReceiptPlanService.updateReceiptPlan(1L, command))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("업데이트 실패 시 예외가 발생한다")
    void updateReceiptPlan_WhenUpdateFails_ThrowsException() {
        // given
        ReceiptPlanCommand command = new ReceiptPlanCommand(
                null,
                20,
                null
        );

        when(getReceiptPlanPort.findReceiptPlanById(1L)).thenReturn(Optional.of(receiptPlan));
        when(updateReceiptPlanPort.updateReceiptPlan(any(ReceiptPlan.class))).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> updateReceiptPlanService.updateReceiptPlan(1L, command))
                .isInstanceOf(ReceiptPlanNotFoundException.class);
    }
}