package com.myme.mywarehome.domains.receipt.application.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.myme.mywarehome.domains.company.application.domain.Company;
import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.product.application.exception.ProductNotFoundException;
import com.myme.mywarehome.domains.product.application.port.out.GetProductPort;
import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.port.in.command.ReceiptPlanCommand;
import com.myme.mywarehome.domains.receipt.application.port.out.CreateReceiptPlanPort;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateReceiptPlanServiceTest {
    @Mock
    private CreateReceiptPlanPort createReceiptPlanPort;

    @Mock
    private GetProductPort getProductPort;

    private CreateReceiptPlanService createReceiptPlanService;

    private Product sampleProduct;
    private ReceiptPlan sampleReceiptPlan;
    private ReceiptPlanCommand sampleCommand;
    private LocalDate sampleDate;

    @BeforeEach
    void setUp() {
        createReceiptPlanService = new CreateReceiptPlanService(createReceiptPlanPort, getProductPort);
        Company company = Company.builder()
                .companyId(1L)
                .companyCode("TEST01")
                .companyName("Test Company")
                .build();

        sampleProduct = Product.builder()
                .productId(1L)
                .productNumber("10000-03P00")
                .productName("Test Product")
                .company(company)
                .build();

        sampleDate = LocalDate.of(2025, 3, 1);

        sampleCommand = new ReceiptPlanCommand(
                "10000-03P00",
                10,
                sampleDate
        );

        sampleReceiptPlan = ReceiptPlan.builder()
                .receiptPlanId(1L)
                .receiptPlanItemCount(10)
                .receiptPlanDate(sampleDate)
                .product(sampleProduct)
                .build();
    }

    @Nested
    @DisplayName("단일 입고 계획 생성 테스트")
    class CreateSingleReceiptPlan {

        @Test
        @DisplayName("입고 계획 생성 성공")
        void createReceiptPlan_withValidCommand_success() {
            // when
            when(getProductPort.findByProductNumber(sampleCommand.productNumber()))
                    .thenReturn(Optional.of(sampleProduct));
            when(createReceiptPlanPort.create(any(ReceiptPlan.class)))
                    .thenReturn(sampleReceiptPlan);

            ReceiptPlan result = createReceiptPlanService.createReceiptPlan(sampleCommand);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getReceiptPlanItemCount()).isEqualTo(sampleCommand.itemCount());
            assertThat(result.getReceiptPlanDate()).isEqualTo(sampleCommand.receiptPlanDate());
            assertThat(result.getProduct().getProductNumber()).isEqualTo(sampleCommand.productNumber());

            verify(getProductPort).findByProductNumber(sampleCommand.productNumber());
            verify(createReceiptPlanPort).create(any(ReceiptPlan.class));
        }

        @Test
        @DisplayName("존재하지 않는 productNumber로 생성 시도시 예외 발생")
        void createReceiptPlan_withNonExistentProduct_throwsProductNotFoundException() {
            // when
            when(getProductPort.findByProductNumber(sampleCommand.productNumber()))
                    .thenReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> createReceiptPlanService.createReceiptPlan(sampleCommand))
                    .isInstanceOf(ProductNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("벌크 입고 계획 생성 테스트")
    class CreateBulkReceiptPlan {
        private List<ReceiptPlanCommand> sampleCommands;
        private List<ReceiptPlan> sampleReceiptPlans;
        private Product sampleProduct2;

        @BeforeEach
        void setUp() {
            sampleProduct2 = Product.builder()
                    .productId(2L)
                    .productNumber("10000-04P00")
                    .productName("Test Product 2")
                    .company(sampleProduct.getCompany())
                    .build();

            ReceiptPlanCommand command2 = new ReceiptPlanCommand(
                    "10000-04P00",
                    20,
                    sampleDate
            );

            sampleCommands = Arrays.asList(sampleCommand, command2);

            ReceiptPlan receiptPlan2 = ReceiptPlan.builder()
                    .receiptPlanId(2L)
                    .receiptPlanItemCount(20)
                    .receiptPlanDate(sampleDate)
                    .product(sampleProduct2)
                    .build();

            sampleReceiptPlans = Arrays.asList(sampleReceiptPlan, receiptPlan2);
        }

        @Test
        @DisplayName("벌크 입고 계획 생성 성공")
        void createReceiptPlanBulk_withValidCommands_success() {
            // when
            when(getProductPort.findAllByProductNumbers(anySet()))
                    .thenReturn(Arrays.asList(sampleProduct, sampleProduct2));
            when(createReceiptPlanPort.createBulk(any()))
                    .thenReturn(sampleReceiptPlans);

            List<ReceiptPlan> results = createReceiptPlanService.createReceiptPlanBulk(sampleCommands);

            // then
            assertThat(results.size()).isEqualTo(2);
            assertThat(results.get(0).getReceiptPlanItemCount()).isEqualTo(sampleCommands.get(0).itemCount());
            assertThat(results.get(1).getReceiptPlanItemCount()).isEqualTo(sampleCommands.get(1).itemCount());

            verify(getProductPort).findAllByProductNumbers(anySet());
            verify(createReceiptPlanPort).createBulk(any());
        }

        @Test
        @DisplayName("일부 물품이 존재하지 않는 경우")
        void createReceiptPlanBulk_withPartialNonExistentProducts_throwsProductNotFoundException() {
            // when
            when(getProductPort.findAllByProductNumbers(anySet()))
                    .thenReturn(List.of(sampleProduct)); // 두 번째 물품은 없음

            // then
            assertThatThrownBy(() -> createReceiptPlanService.createReceiptPlanBulk(sampleCommands))
                    .isInstanceOf(ProductNotFoundException.class);
        }

        @Test
        @DisplayName("중복된 물품 번호로 생성 시도할 경우")
        void createReceiptPlanBulk_withDuplicateProductNumbers_success() {
            // when
            List<ReceiptPlanCommand> duplicateCommands = Arrays.asList(
                    sampleCommand,
                    sampleCommand
            );

            when(getProductPort.findAllByProductNumbers(Set.of(sampleCommand.productNumber())))
                    .thenReturn(List.of(sampleProduct));
            when(createReceiptPlanPort.createBulk(any()))
                    .thenReturn(Arrays.asList(sampleReceiptPlan, sampleReceiptPlan));

            List<ReceiptPlan> results = createReceiptPlanService.createReceiptPlanBulk(duplicateCommands);

            // then
            assertThat(results.size()).isEqualTo(2);
            verify(getProductPort, times(1)).findAllByProductNumbers(any()); // 중복 제품 번호는 한 번만 조회
        }

    }


}