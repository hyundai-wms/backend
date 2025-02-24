package com.myme.mywarehome.domains.issue.application.service;

import com.myme.mywarehome.domains.issue.adapter.out.exception.IssuePlanNotFoundException;
import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.in.command.IssuePlanCommand;
import com.myme.mywarehome.domains.issue.application.port.out.GetIssuePlanPort;
import com.myme.mywarehome.domains.issue.application.port.out.UpdateIssuePlanPort;
import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.product.application.exception.ProductNotFoundException;
import com.myme.mywarehome.domains.product.application.port.out.GetProductPort;
import jakarta.persistence.EntityNotFoundException;
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
class UpdateIssuePlanServiceTest {

    @InjectMocks
    private UpdateIssuePlanService updateIssuePlanService;

    @Mock
    private UpdateIssuePlanPort updateIssuePlanPort;

    @Mock
    private GetProductPort getProductPort;

    @Mock
    private GetIssuePlanPort getIssuePlanPort;

    private Product originalProduct;
    private Product newProduct;
    private IssuePlan issuePlan;
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

        issuePlan = IssuePlan.builder()
                .issuePlanId(1L)
                .issuePlanCode("IP001")
                .issuePlanItemCount(10)
                .issuePlanDate(today)
                .product(originalProduct)
                .build();
    }

    @Test
    @DisplayName("출고 계획의 상품 정보를 수정한다")
    void update_WithNewProduct_UpdatesProductSuccessfully() {
        // given
        IssuePlanCommand command = new IssuePlanCommand(
                newProduct.getProductNumber(),
                null,
                null
        );

        when(getIssuePlanPort.getIssuePlanById(1L)).thenReturn(Optional.of(issuePlan));
        when(getProductPort.findByProductNumber(command.productNumber())).thenReturn(Optional.of(newProduct));
        when(updateIssuePlanPort.update(any(IssuePlan.class))).thenReturn(Optional.of(issuePlan));

        // when
        IssuePlan updatedIssuePlan = updateIssuePlanService.update(1L, command);

        // then
        assertThat(updatedIssuePlan.getProduct().getProductNumber()).isEqualTo(newProduct.getProductNumber());
    }

    @Test
    @DisplayName("출고 계획의 수량을 수정한다")
    void update_WithNewItemCount_UpdatesItemCountSuccessfully() {
        // given
        Integer newItemCount = 20;
        IssuePlanCommand command = new IssuePlanCommand(
                null,
                newItemCount,
                null
        );

        when(getIssuePlanPort.getIssuePlanById(1L)).thenReturn(Optional.of(issuePlan));
        when(updateIssuePlanPort.update(any(IssuePlan.class))).thenReturn(Optional.of(issuePlan));

        // when
        IssuePlan updatedIssuePlan = updateIssuePlanService.update(1L, command);

        // then
        assertThat(updatedIssuePlan.getIssuePlanItemCount()).isEqualTo(newItemCount);
    }

    @Test
    @DisplayName("출고 계획의 날짜를 수정한다")
    void update_WithNewDate_UpdatesDateSuccessfully() {
        // given
        LocalDate newDate = today.plusDays(1);
        IssuePlanCommand command = new IssuePlanCommand(
                null,
                null,
                newDate
        );

        when(getIssuePlanPort.getIssuePlanById(1L)).thenReturn(Optional.of(issuePlan));
        when(updateIssuePlanPort.update(any(IssuePlan.class))).thenReturn(Optional.of(issuePlan));

        // when
        IssuePlan updatedIssuePlan = updateIssuePlanService.update(1L, command);

        // then
        assertThat(updatedIssuePlan.getIssuePlanDate()).isEqualTo(newDate);
    }

    @Test
    @DisplayName("모든 필드를 한번에 수정한다")
    void update_WithAllFields_UpdatesAllFieldsSuccessfully() {
        // given
        Integer newItemCount = 20;
        LocalDate newDate = today.plusDays(1);
        IssuePlanCommand command = new IssuePlanCommand(
                newProduct.getProductNumber(),
                newItemCount,
                newDate
        );

        when(getIssuePlanPort.getIssuePlanById(1L)).thenReturn(Optional.of(issuePlan));
        when(getProductPort.findByProductNumber(command.productNumber())).thenReturn(Optional.of(newProduct));
        when(updateIssuePlanPort.update(any(IssuePlan.class))).thenReturn(Optional.of(issuePlan));

        // when
        IssuePlan updatedIssuePlan = updateIssuePlanService.update(1L, command);

        // then
        assertThat(updatedIssuePlan.getProduct().getProductNumber()).isEqualTo(newProduct.getProductNumber());
        assertThat(updatedIssuePlan.getIssuePlanItemCount()).isEqualTo(newItemCount);
        assertThat(updatedIssuePlan.getIssuePlanDate()).isEqualTo(newDate);
    }

    @Test
    @DisplayName("존재하지 않는 출고 계획 수정 시 예외가 발생한다")
    void update_WithNonExistentIssuePlan_ThrowsException() {
        // given
        IssuePlanCommand command = new IssuePlanCommand(
                null,
                20,
                null
        );

        when(getIssuePlanPort.getIssuePlanById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> updateIssuePlanService.update(999L, command))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("존재하지 않는 상품으로 수정 시 예외가 발생한다")
    void update_WithNonExistentProduct_ThrowsException() {
        // given
        IssuePlanCommand command = new IssuePlanCommand(
                "NON_EXISTENT",
                null,
                null
        );

        when(getIssuePlanPort.getIssuePlanById(1L)).thenReturn(Optional.of(issuePlan));
        when(getProductPort.findByProductNumber(command.productNumber())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> updateIssuePlanService.update(1L, command))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("업데이트 실패 시 예외가 발생한다")
    void update_WhenUpdateFails_ThrowsException() {
        // given
        IssuePlanCommand command = new IssuePlanCommand(
                null,
                20,
                null
        );

        when(getIssuePlanPort.getIssuePlanById(1L)).thenReturn(Optional.of(issuePlan));
        when(updateIssuePlanPort.update(any(IssuePlan.class))).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> updateIssuePlanService.update(1L, command))
                .isInstanceOf(IssuePlanNotFoundException.class);
    }
}