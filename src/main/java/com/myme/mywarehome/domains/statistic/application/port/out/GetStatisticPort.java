package com.myme.mywarehome.domains.statistic.application.port.out;

import com.myme.mywarehome.domains.statistic.adapter.in.web.response.GetStatisticResponse.MonthlyReturnCount;
import com.myme.mywarehome.domains.statistic.adapter.in.web.response.GetStatisticResponse.MonthlyWorkCount;
import com.myme.mywarehome.domains.statistic.adapter.in.web.response.GetStatisticResponse.ReturnWorkInfo;
import java.time.LocalDate;
import java.util.List;

public interface GetStatisticPort {
    // 오늘의 입출고 카운트
    Integer countTodayPlanReceipts(LocalDate selectedDate);
    Integer countTodayReceipts(LocalDate selectedDate);
    Integer countTodayPlanIssues(LocalDate selectedDate);
    Integer countTodayIssues(LocalDate selectedDate);

    // 창고 사용량
    Integer countOccupiedBays();

    // PN 분류별 카운트
    List<Integer> getDefaultPNCount();
    List<Integer> getEngineCount();

    // 월별 작업량/반품량
    List<MonthlyWorkCount> getLastSevenMonthsWorkCount(LocalDate selectedDate);
    List<MonthlyReturnCount> getLastSevenMonthsReturnCount(LocalDate selectedDate);

    // 최근 반품 TOP 10
    List<ReturnWorkInfo> getRecentReturnWorks();

}
