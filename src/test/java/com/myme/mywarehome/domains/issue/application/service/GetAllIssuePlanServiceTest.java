package com.myme.mywarehome.domains.issue.application.service;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.in.command.GetAllIssuePlanCommand;
import com.myme.mywarehome.domains.issue.application.port.out.GetIssuePlanPort;
import com.myme.mywarehome.domains.product.application.domain.Product;
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
class GetAllIssuePlanServiceTest {

    @InjectMocks
    private GetAllIssuePlanService getAllIssuePlanService;

    @Mock
    private GetIssuePlanPort getIssuePlanPort;

    private IssuePlan issuePlan;
    private Product product;
    private Pageable pageable;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();

        product = Product.builder()
                .productNumber("TEST001")
                .productName("테스트 상품")
                .build();

        issuePlan = IssuePlan.builder()
                .issuePlanId(1L)
                .issuePlanItemCount(10)
                .issuePlanDate(today)
                .issuePlanCode("IP001")
                .product(product)
                .build();

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("검색 조건 없이 출고 계획 목록을 조회한다")
    void getAllIssuePlan_WithNoConditions_ReturnsIssuePlanPage() {
        // given
        GetAllIssuePlanCommand command = new GetAllIssuePlanCommand(
                null, null, null, null,
                null, null, null
        );

        List<IssuePlan> issuePlans = List.of(issuePlan);
        Page<IssuePlan> issuePlanPage = new PageImpl<>(issuePlans, pageable, issuePlans.size());

        when(getIssuePlanPort.findAllIssuePlans(any(GetAllIssuePlanCommand.class), any(Pageable.class)))
                .thenReturn(issuePlanPage);

        // when
        Page<IssuePlan> result = getAllIssuePlanService.getAllIssuePlan(command, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("날짜 범위로 출고 계획을 조회한다")
    void getAllIssuePlan_WithDateRange_ReturnsFilteredIssuePlanPage() {
        // given
        GetAllIssuePlanCommand command = new GetAllIssuePlanCommand(
                null, null,
                today.minusDays(1), today.plusDays(1),
                null, null, null
        );

        List<IssuePlan> issuePlans = List.of(issuePlan);
        Page<IssuePlan> issuePlanPage = new PageImpl<>(issuePlans, pageable, issuePlans.size());

        when(getIssuePlanPort.findAllIssuePlans(command, pageable))
                .thenReturn(issuePlanPage);

        // when
        Page<IssuePlan> result = getAllIssuePlanService.getAllIssuePlan(command, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getIssuePlanDate()).isBetween(
                command.issuePlanStartDate(), command.issuePlanEndDate()
        );
    }

    @Test
    @DisplayName("상품 정보로 출고 계획을 조회한다")
    void getAllIssuePlan_WithProductInfo_ReturnsFilteredIssuePlanPage() {
        // given
        GetAllIssuePlanCommand command = new GetAllIssuePlanCommand(
                null, null, null, null,
                product.getProductNumber(), product.getProductName(), null
        );

        List<IssuePlan> issuePlans = List.of(issuePlan);
        Page<IssuePlan> issuePlanPage = new PageImpl<>(issuePlans, pageable, issuePlans.size());

        when(getIssuePlanPort.findAllIssuePlans(command, pageable))
                .thenReturn(issuePlanPage);

        // when
        Page<IssuePlan> result = getAllIssuePlanService.getAllIssuePlan(command, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        IssuePlan foundIssuePlan = result.getContent().get(0);
        assertThat(foundIssuePlan.getProduct().getProductNumber()).isEqualTo(command.productNumber());
        assertThat(foundIssuePlan.getProduct().getProductName()).isEqualTo(command.productName());
    }

    @Test
    @DisplayName("출고 계획 코드로 출고 계획을 조회한다")
    void getAllIssuePlan_WithIssuePlanCode_ReturnsFilteredIssuePlanPage() {
        // given
        GetAllIssuePlanCommand command = new GetAllIssuePlanCommand(
                null, null, null, null,
                null, null, "IP001"
        );

        List<IssuePlan> issuePlans = List.of(issuePlan);
        Page<IssuePlan> issuePlanPage = new PageImpl<>(issuePlans, pageable, issuePlans.size());

        when(getIssuePlanPort.findAllIssuePlans(command, pageable))
                .thenReturn(issuePlanPage);

        // when
        Page<IssuePlan> result = getAllIssuePlanService.getAllIssuePlan(command, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getIssuePlanCode()).isEqualTo(command.issuePlanCode());
    }

    @Test
    @DisplayName("조건에 맞는 출고 계획이 없을 때 빈 페이지를 반환한다")
    void getAllIssuePlan_WithNoMatchingResults_ReturnsEmptyPage() {
        // given
        GetAllIssuePlanCommand command = new GetAllIssuePlanCommand(
                "NON_EXISTENT", null, null, null,
                null, null, null
        );

        Page<IssuePlan> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(getIssuePlanPort.findAllIssuePlans(command, pageable))
                .thenReturn(emptyPage);

        // when
        Page<IssuePlan> result = getAllIssuePlanService.getAllIssuePlan(command, pageable);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }
}