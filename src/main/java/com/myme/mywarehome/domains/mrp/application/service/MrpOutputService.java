package com.myme.mywarehome.domains.mrp.application.service;

import com.myme.mywarehome.domains.mrp.application.domain.MrpOutput;
import com.myme.mywarehome.domains.mrp.application.domain.ProductionPlanningReport;
import com.myme.mywarehome.domains.mrp.application.domain.PurchaseOrderReport;
import com.myme.mywarehome.domains.mrp.application.port.in.MrpOutputUseCase;
import com.myme.mywarehome.domains.mrp.application.port.out.CreateMrpOutputPort;
import com.myme.mywarehome.domains.mrp.application.service.dto.MrpCalculateResultDto;
import com.myme.mywarehome.infrastructure.util.helper.DateFormatHelper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MrpOutputService implements MrpOutputUseCase {
    private final CreateMrpOutputPort createMrpOutputPort;

    @Override
    public void saveResults(MrpCalculateResultDto result) {
        if (result.hasExceptions()) {
            handleExceptionResult(result);
            return;
        }

        // 일정 최적화
        List<PurchaseOrderReport> optimizedPurchaseReports = optimizePurchaseSchedules(result.purchaseOrderReports());
        List<ProductionPlanningReport> optimizedProductionReports = optimizeProductionSchedules(result.productionPlanningReports());

        log.debug("\n\n\n\n");
        log.debug("PurchaseReports : " + optimizedPurchaseReports.size() + " / " + result.purchaseOrderReports().size());
        log.debug("PurchaseReports : " + optimizedProductionReports.size() + " / " + result.productionPlanningReports().size());
        log.debug("\n\n\n\n");

        // MrpOutput 생성 및 저장
        MrpOutput mrpOutput = MrpOutput.builder()
                .createdDate(DateFormatHelper.formatDate(LocalDate.now()))
                .orderedDate(LocalDate.now())
                .canOrder(true)
                .build();

        mrpOutput.assignWithPurchaseOrderReports(optimizedPurchaseReports);
        mrpOutput.assignWithProductionPlanningReports(optimizedProductionReports);

        createMrpOutputPort.createMrpOutput(
                mrpOutput,
                optimizedPurchaseReports,
                optimizedProductionReports,
                Collections.emptyList()
        );
    }

    private List<PurchaseOrderReport> optimizePurchaseSchedules(List<PurchaseOrderReport> reports) {
        return mergeSameProductSchedules(reports,
                // 같은 제품인 경우에만 병합 가능
                (r1, r2) -> r1.getProduct().getProductId().equals(r2.getProduct().getProductId()),
                // 병합 시 최적의 일정 계산
                (r1, r2) -> PurchaseOrderReport.builder()
                        .purchaseOrderDate(getEarlierDate(r1.getPurchaseOrderDate(), r2.getPurchaseOrderDate()))
                        .receiptPlanDate(getLaterDate(r1.getReceiptPlanDate(), r2.getReceiptPlanDate()))
                        .product(r1.getProduct())
                        .quantity(r1.getQuantity() + r2.getQuantity())
                        .build()
        );
    }

    private List<ProductionPlanningReport> optimizeProductionSchedules(List<ProductionPlanningReport> reports) {
        return mergeSameProductSchedules(reports,
                (r1, r2) -> r1.getProduct().getProductId().equals(r2.getProduct().getProductId()),
                (r1, r2) -> ProductionPlanningReport.builder()
                        .productionPlanningDate(getEarlierDate(r1.getProductionPlanningDate(), r2.getProductionPlanningDate()))
                        .issuePlanDate(getLaterDate(r1.getIssuePlanDate(), r2.getIssuePlanDate()))
                        .product(r1.getProduct())
                        .quantity(r1.getQuantity() + r2.getQuantity())
                        .build()
        );
    }

    private LocalDate getEarlierDate(LocalDate date1, LocalDate date2) {
        return date1.isBefore(date2) ? date1 : date2;
    }

    private LocalDate getLaterDate(LocalDate date1, LocalDate date2) {
        return date1.isAfter(date2) ? date1 : date2;
    }

    private <T> List<T> mergeSameProductSchedules(
            List<T> reports,
            BiPredicate<T, T> isSameSchedule,
            BiFunction<T, T, T> mergeSchedules) {

        if (reports.isEmpty()) return Collections.emptyList();

        List<T> optimized = new ArrayList<>();
        T current = reports.get(0);

        for (int i = 1; i < reports.size(); i++) {
            T next = reports.get(i);
            if (isSameSchedule.test(current, next)) {
                current = mergeSchedules.apply(current, next);
            } else {
                optimized.add(current);
                current = next;
            }
        }
        optimized.add(current);

        return optimized;
    }

    private void handleExceptionResult(MrpCalculateResultDto result) {
        MrpOutput mrpOutput = MrpOutput.builder()
                .createdDate(DateFormatHelper.formatDate(LocalDate.now()))
                .orderedDate(LocalDate.now())
                .canOrder(false)
                .build();

        mrpOutput.assignWithMrpExceptionReports(result.mrpExceptionReports());

        createMrpOutputPort.createMrpOutput(
                mrpOutput,
                Collections.emptyList(),
                Collections.emptyList(),
                result.mrpExceptionReports()
        );
    }
}
