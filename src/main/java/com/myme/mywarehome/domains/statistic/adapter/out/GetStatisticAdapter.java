package com.myme.mywarehome.domains.statistic.adapter.out;

import com.myme.mywarehome.domains.issue.adapter.out.persistence.IssueJpaRepository;
import com.myme.mywarehome.domains.issue.adapter.out.persistence.IssuePlanJpaRepository;
import com.myme.mywarehome.domains.product.adapter.out.persistence.ProductJpaRepository;
import com.myme.mywarehome.domains.receipt.adapter.out.persistence.ReceiptJpaRepository;
import com.myme.mywarehome.domains.receipt.adapter.out.persistence.ReceiptPlanJpaRepository;
import com.myme.mywarehome.domains.receipt.adapter.out.persistence.ReturnJpaRepository;
import com.myme.mywarehome.domains.statistic.adapter.in.web.response.GetStatisticResponse.MonthlyReturnCount;
import com.myme.mywarehome.domains.statistic.adapter.in.web.response.GetStatisticResponse.MonthlyWorkCount;
import com.myme.mywarehome.domains.statistic.adapter.in.web.response.GetStatisticResponse.ReturnWorkInfo;
import com.myme.mywarehome.domains.statistic.application.port.out.GetStatisticPort;
import com.myme.mywarehome.domains.stock.adapter.out.persistence.BayJpaRepository;
import com.myme.mywarehome.domains.stock.adapter.out.persistence.StockJpaRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetStatisticAdapter implements GetStatisticPort {
    private final ReceiptPlanJpaRepository receiptPlanJpaRepository;
    private final ReceiptJpaRepository receiptJpaRepository;
    private final IssuePlanJpaRepository issuePlanJpaRepository;
    private final IssueJpaRepository issueJpaRepository;
    private final BayJpaRepository bayJpaRepository;
    private final ReturnJpaRepository returnJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final StockJpaRepository stockJpaRepository;


    @Override
    public Integer countTodayPlanReceipts(LocalDate selectedDate) {
        return receiptPlanJpaRepository.countByReceiptPlanDate(selectedDate);
    }

    @Override
    public Integer countTodayReceipts(LocalDate selectedDate) {
        return receiptJpaRepository.countByReceiptDate(selectedDate);
    }

    @Override
    public Integer countTodayPlanIssues(LocalDate selectedDate) {
        return issuePlanJpaRepository.countByIssuePlanDate(selectedDate);
    }

    @Override
    public Integer countTodayIssues(LocalDate selectedDate) {
        return issueJpaRepository.countByIssueDate(selectedDate);
    }

    @Override
    public Integer countOccupiedBays() {
        return bayJpaRepository.countOccupiedBays();
    }

    @Override
    public List<Integer> getDefaultPNCount() {
        return productJpaRepository.countByDefaultPN();
    }

    @Override
    public List<Integer> getEngineCount() {
        return productJpaRepository.countByEngineType();
    }

    @Override
    public List<MonthlyWorkCount> getLastSevenMonthsWorkCount(LocalDate selectedDate) {
        List<MonthlyWorkCount> result = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate targetMonth = selectedDate.minusMonths(i);
            Integer receiptCount = receiptJpaRepository.countByMonth(targetMonth);
            Integer issueCount = issueJpaRepository.countByMonth(targetMonth);

            result.add(new MonthlyWorkCount(
                    targetMonth.getMonthValue() + "월",
                    Arrays.asList(receiptCount, issueCount)
            ));
        }
        return result;
    }

    @Override
    public List<MonthlyReturnCount> getLastSevenMonthsReturnCount(LocalDate selectedDate) {
        List<MonthlyReturnCount> result = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate targetMonth = selectedDate.minusMonths(i);
            Integer returnCount = returnJpaRepository.countByMonth(targetMonth);

            result.add(new MonthlyReturnCount(
                    targetMonth.getMonthValue() + "월",
                    returnCount
            ));
        }
        return result;
    }

    @Override
    public List<ReturnWorkInfo> getRecentReturnWorks() {
        // 최근 반품 TOP 10 조회
        return returnJpaRepository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(returnRecord -> new ReturnWorkInfo(
                        returnRecord.getReturnDate().toString(),
                        returnRecord.getReceiptPlan().getProduct().getProductName(),
                        returnRecord.getReceiptPlan().getProduct().getProductNumber(),
                        returnRecord.getReceiptPlan().getProduct().getCompany().getCompanyName()
                ))
                .toList();
    }
}
