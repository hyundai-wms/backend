package com.myme.mywarehome.domains.issue.application.service;

import com.myme.mywarehome.domains.issue.adapter.out.exception.IssuePlanNotFoundException;
import com.myme.mywarehome.domains.issue.application.port.in.result.TodayIssueResult;
import com.myme.mywarehome.domains.issue.application.port.out.GetIssuePlanPort;
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
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetTodayIssueServiceTest {

    @InjectMocks
    private GetTodayIssueService getTodayIssueService;

    @Mock
    private GetIssuePlanPort getIssuePlanPort;

    private LocalDate today;
    private TodayIssueResult todayIssueResult;
    private Pageable pageable;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        now = LocalDateTime.now();
        pageable = PageRequest.of(0, 10);

        todayIssueResult = new TodayIssueResult(
                1L,                     // issuePlanId
                "IP001",               // issuePlanCode
                today,                 // issuePlanDate
                5L,                    // issueCount
                10L,                   // totalItemCount
                "PENDING",             // issueStatus
                "TEST001",             // productNumber
                "테스트 상품",           // productName
                1L,                    // companyId
                "COMPANY001",          // companyCode
                "테스트 업체",           // companyName
                now.minusHours(1),     // createdAt
                now                    // updatedAt
        );
    }

    @Test
    @DisplayName("선택한 날짜의 출고 계획 목록을 조회한다")
    void getTodayIssue_WithSelectedDate_ReturnsTodayIssues() {
        // given
        List<TodayIssueResult> issues = List.of(todayIssueResult);
        Page<TodayIssueResult> issuePage = new PageImpl<>(issues, pageable, issues.size());

        when(getIssuePlanPort.findTodayIssues(today, pageable))
                .thenReturn(issuePage);

        // when
        Page<TodayIssueResult> result = getTodayIssueService.getTodayIssue(today, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        TodayIssueResult foundResult = result.getContent().get(0);
        assertThat(foundResult.issuePlanId()).isEqualTo(todayIssueResult.issuePlanId());
        assertThat(foundResult.issuePlanCode()).isEqualTo(todayIssueResult.issuePlanCode());
        assertThat(foundResult.issuePlanDate()).isEqualTo(today);
        assertThat(foundResult.issueCount()).isEqualTo(todayIssueResult.issueCount());
        assertThat(foundResult.totalItemCount()).isEqualTo(todayIssueResult.totalItemCount());
        assertThat(foundResult.issueStatus()).isEqualTo("PENDING");
        assertThat(foundResult.productNumber()).isEqualTo(todayIssueResult.productNumber());
        assertThat(foundResult.productName()).isEqualTo(todayIssueResult.productName());
        assertThat(foundResult.companyId()).isEqualTo(todayIssueResult.companyId());
        assertThat(foundResult.companyCode()).isEqualTo(todayIssueResult.companyCode());
        assertThat(foundResult.companyName()).isEqualTo(todayIssueResult.companyName());
    }

    @Test
    @DisplayName("선택한 날짜에 출고 계획이 없으면 빈 페이지를 반환한다")
    void getTodayIssue_WithNoIssues_ReturnsEmptyPage() {
        // given
        Page<TodayIssueResult> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(getIssuePlanPort.findTodayIssues(today, pageable))
                .thenReturn(emptyPage);

        // when
        Page<TodayIssueResult> result = getTodayIssueService.getTodayIssue(today, pageable);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    @DisplayName("특정 출고 계획을 ID로 조회한다")
    void getTodayIssueById_WithValidId_ReturnsTodayIssue() {
        // given
        when(getIssuePlanPort.findTodayIssueById(1L, today))
                .thenReturn(Optional.of(todayIssueResult));

        // when
        TodayIssueResult result = getTodayIssueService.getTodayIssueById(1L, today);

        // then
        assertThat(result).isNotNull();
        assertThat(result.issuePlanId()).isEqualTo(todayIssueResult.issuePlanId());
        assertThat(result.issuePlanCode()).isEqualTo(todayIssueResult.issuePlanCode());
        assertThat(result.issueStatus()).isEqualTo("PENDING");
        assertThat(result.issueCount()).isEqualTo(todayIssueResult.issueCount());
        assertThat(result.totalItemCount()).isEqualTo(todayIssueResult.totalItemCount());
    }

    @Test
    @DisplayName("존재하지 않는 출고 계획 ID로 조회시 예외가 발생한다")
    void getTodayIssueById_WithInvalidId_ThrowsException() {
        // given
        when(getIssuePlanPort.findTodayIssueById(999L, today))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> getTodayIssueService.getTodayIssueById(999L, today))
                .isInstanceOf(IssuePlanNotFoundException.class);
    }

    @Test
    @DisplayName("실시간 출고 계획 현황을 구독한다")
    void subscribeTodayIssues_ReturnsFlux() {
        // given
        Flux<ServerSentEvent<Object>> expectedFlux = Flux.just(
                ServerSentEvent.builder().data(todayIssueResult).build()
        );

        when(getIssuePlanPort.subscribeTodayIssues(eq(today), eq(0), eq(10)))
                .thenReturn(expectedFlux);

        // when
        Flux<ServerSentEvent<Object>> result = getTodayIssueService.subscribeTodayIssues(today, 0, 10);

        // then
        StepVerifier.create(result)
                .assertNext(event -> {
                    assertThat(event.data()).isInstanceOf(TodayIssueResult.class);
                    TodayIssueResult data = (TodayIssueResult) event.data();
                    assertThat(data.issuePlanId()).isEqualTo(todayIssueResult.issuePlanId());
                    assertThat(data.issueStatus()).isEqualTo(todayIssueResult.issueStatus());
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("출고 계획 현황 업데이트를 알린다")
    void notifyIssueUpdate_NotifiesSubscribers() {
        // when
        getTodayIssueService.notifyIssueUpdate(todayIssueResult, today);

        // then
        verify(getIssuePlanPort, times(1))
                .emitTodayIssueUpdate(eq(todayIssueResult), eq(today));
    }
}