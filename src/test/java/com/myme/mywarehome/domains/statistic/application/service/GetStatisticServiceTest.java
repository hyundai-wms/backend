package com.myme.mywarehome.domains.statistic.application.service;

import com.myme.mywarehome.domains.statistic.adapter.in.web.response.GetStatisticResponse;
import com.myme.mywarehome.domains.statistic.application.port.out.GetStatisticPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GetStatisticServiceTest {

    @Mock
    private GetStatisticPort getStatisticPort;

    private GetStatisticService getStatisticService;

    @BeforeEach
    void setUp() {
        getStatisticService = new GetStatisticService(getStatisticPort);
    }

    @Test
    @DisplayName("선택된 날짜의 통계 데이터를 조회한다")
    void getStatistic_withSelectedDate_returnsStatistics() {
        // given
        LocalDate selectedDate = LocalDate.of(2024, 2, 24);

        // 기본 카운트 데이터
        Integer receiptPlanCount = 10;
        Integer receiptCount = 8;
        Integer issuePlanCount = 15;
        Integer issueCount = 12;
        Integer warehouseUsageCount = 250;
        Integer warehouseCount = 3200;

        // 배열 데이터
        List<Integer> defaultPNCountArray = List.of(100, 150, 200);
        List<Integer> engineCountArray = List.of(50, 75, 100);

        // 월별 작업량 데이터
        List<GetStatisticResponse.MonthlyWorkCount> workCount = List.of(
                GetStatisticResponse.createMonthlyWorkCount("2024-01", List.of(100, 80)),
                GetStatisticResponse.createMonthlyWorkCount("2024-02", List.of(120, 90))
        );

        // 월별 반품량 데이터
        List<GetStatisticResponse.MonthlyReturnCount> returnCount = List.of(
                GetStatisticResponse.createMonthlyReturnCount("2024-01", 5),
                GetStatisticResponse.createMonthlyReturnCount("2024-02", 3)
        );

        // 최근 반품 작업 정보
        List<GetStatisticResponse.ReturnWorkInfo> returnWorkArray = List.of(
                GetStatisticResponse.createReturnWorkInfo(
                        "2024-02-24", "제품A", "PN001", "회사A"
                ),
                GetStatisticResponse.createReturnWorkInfo(
                        "2024-02-23", "제품B", "PN002", "회사B"
                )
        );

        given(getStatisticPort.countTodayPlanReceipts(selectedDate)).willReturn(receiptPlanCount);
        given(getStatisticPort.countTodayReceipts(selectedDate)).willReturn(receiptCount);
        given(getStatisticPort.countTodayPlanIssues(selectedDate)).willReturn(issuePlanCount);
        given(getStatisticPort.countTodayIssues(selectedDate)).willReturn(issueCount);
        given(getStatisticPort.countOccupiedBays()).willReturn(warehouseUsageCount);
        given(getStatisticPort.getDefaultPNCount()).willReturn(defaultPNCountArray);
        given(getStatisticPort.getEngineCount()).willReturn(engineCountArray);
        given(getStatisticPort.getLastSevenMonthsWorkCount(selectedDate)).willReturn(workCount);
        given(getStatisticPort.getLastSevenMonthsReturnCount(selectedDate)).willReturn(returnCount);
        given(getStatisticPort.getRecentReturnWorks()).willReturn(returnWorkArray);

        // when
        GetStatisticResponse response = getStatisticService.getStatistic(selectedDate);

        // then
        assertThat(response).isNotNull();
        assertThat(response.receiptPlanCount()).isEqualTo(receiptPlanCount);
        assertThat(response.receiptCount()).isEqualTo(receiptCount);
        assertThat(response.issuePlanCount()).isEqualTo(issuePlanCount);
        assertThat(response.issueCount()).isEqualTo(issueCount);
        assertThat(response.warehouseUsageCount()).isEqualTo(warehouseUsageCount);
        assertThat(response.warehouseCount()).isEqualTo(warehouseCount);
        assertThat(response.defaultPNCountArray()).isEqualTo(defaultPNCountArray);
        assertThat(response.engineCountArray()).isEqualTo(engineCountArray);

        assertThat(response.workCount())
                .hasSize(2)
                .containsExactlyElementsOf(workCount);

        assertThat(response.returnCount())
                .hasSize(2)
                .containsExactlyElementsOf(returnCount);

        assertThat(response.returnWorkArray())
                .hasSize(2)
                .containsExactlyElementsOf(returnWorkArray);

        // Port 메서드 호출 검증
        verify(getStatisticPort).countTodayPlanReceipts(selectedDate);
        verify(getStatisticPort).countTodayReceipts(selectedDate);
        verify(getStatisticPort).countTodayPlanIssues(selectedDate);
        verify(getStatisticPort).countTodayIssues(selectedDate);
        verify(getStatisticPort).countOccupiedBays();
        verify(getStatisticPort).getDefaultPNCount();
        verify(getStatisticPort).getEngineCount();
        verify(getStatisticPort).getLastSevenMonthsWorkCount(selectedDate);
        verify(getStatisticPort).getLastSevenMonthsReturnCount(selectedDate);
        verify(getStatisticPort).getRecentReturnWorks();
    }

    @Test
    @DisplayName("데이터가 없는 경우 빈 통계 데이터를 반환한다")
    void getStatistic_whenNoData_returnsEmptyStatistics() {
        // given
        LocalDate selectedDate = LocalDate.of(2024, 2, 24);
        List<Integer> emptyArray = List.of();
        List<GetStatisticResponse.MonthlyWorkCount> emptyWorkCount = List.of();
        List<GetStatisticResponse.MonthlyReturnCount> emptyReturnCount = List.of();
        List<GetStatisticResponse.ReturnWorkInfo> emptyReturnWorkArray = List.of();

        given(getStatisticPort.countTodayPlanReceipts(selectedDate)).willReturn(0);
        given(getStatisticPort.countTodayReceipts(selectedDate)).willReturn(0);
        given(getStatisticPort.countTodayPlanIssues(selectedDate)).willReturn(0);
        given(getStatisticPort.countTodayIssues(selectedDate)).willReturn(0);
        given(getStatisticPort.countOccupiedBays()).willReturn(0);
        given(getStatisticPort.getDefaultPNCount()).willReturn(emptyArray);
        given(getStatisticPort.getEngineCount()).willReturn(emptyArray);
        given(getStatisticPort.getLastSevenMonthsWorkCount(selectedDate)).willReturn(emptyWorkCount);
        given(getStatisticPort.getLastSevenMonthsReturnCount(selectedDate)).willReturn(emptyReturnCount);
        given(getStatisticPort.getRecentReturnWorks()).willReturn(emptyReturnWorkArray);

        // when
        GetStatisticResponse response = getStatisticService.getStatistic(selectedDate);

        // then
        assertThat(response).isNotNull();
        assertThat(response.receiptPlanCount()).isZero();
        assertThat(response.receiptCount()).isZero();
        assertThat(response.issuePlanCount()).isZero();
        assertThat(response.issueCount()).isZero();
        assertThat(response.warehouseUsageCount()).isZero();
        assertThat(response.warehouseCount()).isEqualTo(3200);  // 고정값
        assertThat(response.defaultPNCountArray()).isEmpty();
        assertThat(response.engineCountArray()).isEmpty();
        assertThat(response.workCount()).isEmpty();
        assertThat(response.returnCount()).isEmpty();
        assertThat(response.returnWorkArray()).isEmpty();
    }
}