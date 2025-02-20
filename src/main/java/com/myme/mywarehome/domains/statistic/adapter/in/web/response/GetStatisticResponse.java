package com.myme.mywarehome.domains.statistic.adapter.in.web.response;

import java.util.List;

public record GetStatisticResponse(
        Integer receiptPlanCount,
        Integer receiptCount,
        Integer issuePlanCount,
        Integer issueCount,
        Integer warehouseUsageCount,
        Integer warehouseCount,
        List<Integer> defaultPNCountArray,
        List<Integer> engineCountArray,
        List<MonthlyWorkCount> workCount,
        List<MonthlyReturnCount> returnCount,
        List<ReturnWorkInfo> returnWorkArray
) {
    public record MonthlyWorkCount(
            String date,
            List<Integer> workCount  // [입고량, 출고량]
    ) {}

    public record MonthlyReturnCount(
            String date,
            Integer returnCount
    ) {}

    public record ReturnWorkInfo(
            String returnDate,
            String returnProductName,
            String returnProductNumber,
            String returnCompanyName
    ) {}

    // Record를 생성하기 위한 정적 팩토리 메서드
    public static GetStatisticResponse of(
            Integer receiptPlanCount,
            Integer receiptCount,
            Integer issuePlanCount,
            Integer issueCount,
            Integer warehouseUsageCount,
            Integer warehouseCount,
            List<Integer> defaultPNCountArray,
            List<Integer> engineCountArray,
            List<MonthlyWorkCount> workCount,
            List<MonthlyReturnCount> returnCount,
            List<ReturnWorkInfo> returnWorkArray
    ) {
        return new GetStatisticResponse(
                receiptPlanCount,
                receiptCount,
                issuePlanCount,
                issueCount,
                warehouseUsageCount,
                warehouseCount,
                defaultPNCountArray,
                engineCountArray,
                workCount,
                returnCount,
                returnWorkArray
        );
    }

    public static MonthlyWorkCount createMonthlyWorkCount(String date, List<Integer> workCount) {
        return new MonthlyWorkCount(date, workCount);
    }

    public static MonthlyReturnCount createMonthlyReturnCount(String date, Integer returnCount) {
        return new MonthlyReturnCount(date, returnCount);
    }

    public static ReturnWorkInfo createReturnWorkInfo(
            String returnDate,
            String returnProductName,
            String returnProductNumber,
            String returnCompanyName
    ) {
        return new ReturnWorkInfo(returnDate, returnProductName, returnProductNumber, returnCompanyName);
    }
}