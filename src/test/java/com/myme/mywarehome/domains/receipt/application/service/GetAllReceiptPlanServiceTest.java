package com.myme.mywarehome.domains.receipt.application.service;

import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.port.in.command.GetAllReceiptPlanCommand;
import com.myme.mywarehome.domains.receipt.application.port.out.GetReceiptPlanPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllReceiptPlanServiceTest {

    @InjectMocks
    private GetAllReceiptPlanService getAllReceiptPlanService;

    @Mock
    private GetReceiptPlanPort getReceiptPlanPort;

    private ReceiptPlan receiptPlan;
    private Product product;
    private Pageable pageable;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        pageable = PageRequest.of(0, 10);

        product = Product.builder()
                .productNumber("TEST001")
                .productName("테스트 상품")
                .build();

        receiptPlan = ReceiptPlan.builder()
                .receiptPlanId(1L)
                .receiptPlanCode("RP001")
                .receiptPlanItemCount(10)
                .receiptPlanDate(today)
                .product(product)
                .build();
    }

    @Test
    @DisplayName("검색 조건 없이 입고 계획 목록을 조회한다")
    void getAllReceiptPlan_WithNoConditions_ReturnsReceiptPlanPage() {
        // given
        GetAllReceiptPlanCommand command = new GetAllReceiptPlanCommand(
                null, null, null, null,
                null, null, null
        );

        List<ReceiptPlan> receiptPlans = List.of(receiptPlan);
        Page<ReceiptPlan> receiptPlanPage = new PageImpl<>(receiptPlans, pageable, receiptPlans.size());

        when(getReceiptPlanPort.findAllReceiptPlans(any(GetAllReceiptPlanCommand.class), any(Pageable.class), any(LocalDate.class)))
                .thenReturn(receiptPlanPage);

        // when
        Page<ReceiptPlan> result = getAllReceiptPlanService.getAllReceiptPlan(command, pageable, today);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getNumber()).isZero();
        assertThat(result.getSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("날짜로 입고 계획을 조회한다")
    void getAllReceiptPlan_WithDateFilter_ReturnsFilteredReceiptPlanPage() {
        // given
        LocalDate selectedDate = today.plusDays(1);
        GetAllReceiptPlanCommand command = new GetAllReceiptPlanCommand(
                null, null, null, null,
                null, null, null
        );

        List<ReceiptPlan> receiptPlans = List.of(receiptPlan);
        Page<ReceiptPlan> receiptPlanPage = new PageImpl<>(receiptPlans, pageable, receiptPlans.size());

        when(getReceiptPlanPort.findAllReceiptPlans(command, pageable, selectedDate))
                .thenReturn(receiptPlanPage);

        // when
        Page<ReceiptPlan> result = getAllReceiptPlanService.getAllReceiptPlan(command, pageable, selectedDate);

        // then
        assertThat(result.getContent()).hasSize(1);
        ReceiptPlan foundReceiptPlan = result.getContent().get(0);
        assertThat(foundReceiptPlan.getReceiptPlanId()).isEqualTo(receiptPlan.getReceiptPlanId());
    }

    @Test
    @DisplayName("조건에 맞는 입고 계획이 없을 때 빈 페이지를 반환한다")
    void getAllReceiptPlan_WithNoMatchingResults_ReturnsEmptyPage() {
        // given
        GetAllReceiptPlanCommand command = new GetAllReceiptPlanCommand(
                "NON_EXISTENT", null, null, null,
                null, null, null
        );

        Page<ReceiptPlan> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(getReceiptPlanPort.findAllReceiptPlans(command, pageable, today))
                .thenReturn(emptyPage);

        // when
        Page<ReceiptPlan> result = getAllReceiptPlanService.getAllReceiptPlan(command, pageable, today);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("페이지 크기를 지정하여 입고 계획을 조회한다")
    void getAllReceiptPlan_WithCustomPageSize_ReturnsPagedReceiptPlans() {
        // given
        Pageable customPageable = PageRequest.of(0, 5);
        GetAllReceiptPlanCommand command = new GetAllReceiptPlanCommand(
                null, null, null, null,
                null, null, null
        );

        List<ReceiptPlan> receiptPlans = List.of(receiptPlan);
        Page<ReceiptPlan> receiptPlanPage = new PageImpl<>(receiptPlans, customPageable, receiptPlans.size());

        when(getReceiptPlanPort.findAllReceiptPlans(command, customPageable, today))
                .thenReturn(receiptPlanPage);

        // when
        Page<ReceiptPlan> result = getAllReceiptPlanService.getAllReceiptPlan(command, customPageable, today);

        // then
        assertThat(result.getSize()).isEqualTo(5);
        assertThat(result.getContent()).hasSize(1);
    }
}