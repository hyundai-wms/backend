package com.myme.mywarehome.domains.issue.application.service;

import com.myme.mywarehome.domains.issue.application.domain.Issue;
import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.in.command.GetAllIssueCommand;
import com.myme.mywarehome.domains.issue.application.port.out.GetAllIssuePort;
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
class GetAllIssueServiceTest {

    @InjectMocks
    private GetAllIssueService getAllIssueService;

    @Mock
    private GetAllIssuePort getAllIssuePort;

    private Issue issue;
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
                .issuePlanCode("IP001")
                .issuePlanDate(today)
                .issuePlanItemCount(10)
                .product(product)
                .build();

        issue = Issue.builder()
                .issueId(1L)
                .issueCode("IS001")
                .issueDate(today)
                .issuePlan(issuePlan)
                .product(product)
                .build();

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("검색 조건 없이 출고 목록을 조회한다")
    void getAllIssue_WithNoConditions_ReturnsIssuePage() {
        // given
        GetAllIssueCommand command = new GetAllIssueCommand(
                null, null, null, null, null,
                null, null, null, null, null
        );

        List<Issue> issues = List.of(issue);
        Page<Issue> issuePage = new PageImpl<>(issues, pageable, issues.size());

        when(getAllIssuePort.findAllIssues(any(GetAllIssueCommand.class), any(Pageable.class)))
                .thenReturn(issuePage);

        // when
        Page<Issue> result = getAllIssueService.getAllIssue(command, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("업체 정보로 출고 목록을 조회한다")
    void getAllIssue_WithCompanyInfo_ReturnsFilteredIssuePage() {
        // given
        GetAllIssueCommand command = new GetAllIssueCommand(
                "COMPANY001", "테스트 업체",
                null, null, null,
                null, null, null, null, null
        );

        List<Issue> issues = List.of(issue);
        Page<Issue> issuePage = new PageImpl<>(issues, pageable, issues.size());

        when(getAllIssuePort.findAllIssues(command, pageable))
                .thenReturn(issuePage);

        // when
        Page<Issue> result = getAllIssueService.getAllIssue(command, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("출고 계획 정보로 출고 목록을 조회한다")
    void getAllIssue_WithIssuePlanInfo_ReturnsFilteredIssuePage() {
        // given
        GetAllIssueCommand command = new GetAllIssueCommand(
                null, null,
                "IP001", today.minusDays(1), today.plusDays(1),
                null, null, null, null, null
        );

        List<Issue> issues = List.of(issue);
        Page<Issue> issuePage = new PageImpl<>(issues, pageable, issues.size());

        when(getAllIssuePort.findAllIssues(command, pageable))
                .thenReturn(issuePage);

        // when
        Page<Issue> result = getAllIssueService.getAllIssue(command, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        Issue foundIssue = result.getContent().get(0);
        assertThat(foundIssue.getIssuePlan().getIssuePlanCode()).isEqualTo(command.issuePlanCode());
        assertThat(foundIssue.getIssuePlan().getIssuePlanDate())
                .isBetween(command.issuePlanStartDate(), command.issuePlanEndDate());
    }

    @Test
    @DisplayName("상품 정보로 출고 목록을 조회한다")
    void getAllIssue_WithProductInfo_ReturnsFilteredIssuePage() {
        // given
        GetAllIssueCommand command = new GetAllIssueCommand(
                null, null, null, null, null,
                product.getProductNumber(), product.getProductName(),
                null, null, null
        );

        List<Issue> issues = List.of(issue);
        Page<Issue> issuePage = new PageImpl<>(issues, pageable, issues.size());

        when(getAllIssuePort.findAllIssues(command, pageable))
                .thenReturn(issuePage);

        // when
        Page<Issue> result = getAllIssueService.getAllIssue(command, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        Issue foundIssue = result.getContent().get(0);
        assertThat(foundIssue.getProduct().getProductNumber()).isEqualTo(command.productNumber());
        assertThat(foundIssue.getProduct().getProductName()).isEqualTo(command.productName());
    }

    @Test
    @DisplayName("출고 정보로 출고 목록을 조회한다")
    void getAllIssue_WithIssueInfo_ReturnsFilteredIssuePage() {
        // given
        GetAllIssueCommand command = new GetAllIssueCommand(
                null, null, null, null, null,
                null, null,
                "IS001", today.minusDays(1), today.plusDays(1)
        );

        List<Issue> issues = List.of(issue);
        Page<Issue> issuePage = new PageImpl<>(issues, pageable, issues.size());

        when(getAllIssuePort.findAllIssues(command, pageable))
                .thenReturn(issuePage);

        // when
        Page<Issue> result = getAllIssueService.getAllIssue(command, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        Issue foundIssue = result.getContent().get(0);
        assertThat(foundIssue.getIssueCode()).isEqualTo(command.issueCode());
        assertThat(foundIssue.getIssueDate())
                .isBetween(command.issueStartDate(), command.issueEndDate());
    }

    @Test
    @DisplayName("조건에 맞는 출고가 없을 때 빈 페이지를 반환한다")
    void getAllIssue_WithNoMatchingResults_ReturnsEmptyPage() {
        // given
        GetAllIssueCommand command = new GetAllIssueCommand(
                "NON_EXISTENT", null, null, null, null,
                null, null, null, null, null
        );

        Page<Issue> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(getAllIssuePort.findAllIssues(command, pageable))
                .thenReturn(emptyPage);

        // when
        Page<Issue> result = getAllIssueService.getAllIssue(command, pageable);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }
}