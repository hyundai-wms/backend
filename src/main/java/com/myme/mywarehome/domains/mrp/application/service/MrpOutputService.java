package com.myme.mywarehome.domains.mrp.application.service;

import com.myme.mywarehome.domains.mrp.application.domain.MrpOutput;
import com.myme.mywarehome.domains.mrp.application.domain.ProductionPlanningReport;
import com.myme.mywarehome.domains.mrp.application.domain.PurchaseOrderReport;
import com.myme.mywarehome.domains.mrp.application.port.in.MrpOutputUseCase;
import com.myme.mywarehome.domains.mrp.application.port.out.CreateMrpOutputPort;
import com.myme.mywarehome.domains.mrp.application.port.out.CreateMrpReportFilePort;
import com.myme.mywarehome.domains.mrp.application.port.out.UpdateMrpOutputPort;
import com.myme.mywarehome.domains.mrp.application.service.dto.MrpCalculateResultDto;
import com.myme.mywarehome.infrastructure.util.helper.DateFormatHelper;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MrpOutputService implements MrpOutputUseCase {
    private final CreateMrpOutputPort createMrpOutputPort;
    private final UpdateMrpOutputPort updateMrpOutputPort;
    private final CreateMrpReportFilePort createMrpReportFilePort;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    @Override
    @Transactional
    public void saveResults(MrpCalculateResultDto result) {
        // 이전 주문들 비활성화
        updateMrpOutputPort.deactivatePreviousOrders();

        if (result.hasExceptions()) {
            handleExceptionResult(result);
            return;
        }

        // 일정 최적화
        List<PurchaseOrderReport> optimizedPurchaseReports = optimizePurchaseSchedules(result.purchaseOrderReports());
        List<ProductionPlanningReport> optimizedProductionReports = optimizeProductionSchedules(result.productionPlanningReports());


        // MrpOutput 생성 및 저장
        MrpOutput mrpOutput = MrpOutput.builder()
                .createdDate(DateFormatHelper.formatDate(LocalDate.now()))
                .orderedDate(LocalDate.now())
                .canOrder(true)
                .isOrdered(false)
                .build();

        mrpOutput.assignWithPurchaseOrderReports(optimizedPurchaseReports);
        mrpOutput.assignWithProductionPlanningReports(optimizedProductionReports);

        createMrpOutputPort.createMrpOutput(
                mrpOutput,
                optimizedPurchaseReports,
                optimizedProductionReports,
                Collections.emptyList()
        );

        // Excel 파일 생성 및 S3 업로드
        String purchaseOrderReportLink = null;
        String productionPlanningReportLink = null;

        if ("prod".equals(activeProfile)) {
            purchaseOrderReportLink = createMrpReportFilePort.createAndUploadPurchaseOrderReport(mrpOutput, optimizedPurchaseReports);
            productionPlanningReportLink = createMrpReportFilePort.createAndUploadProductionPlanningReport(mrpOutput, optimizedProductionReports);
        }

        // 생성된 링크 업데이트
        mrpOutput.addPurchaseOrderReportLink(purchaseOrderReportLink);
        mrpOutput.addProductionPlanningReportLink(productionPlanningReportLink);
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
                        .safeItemCount(r1.getSafeItemCount() + r2.getSafeItemCount())
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
                        .safeItemCount(r1.getSafeItemCount() + r2.getSafeItemCount())
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
                .isOrdered(false)
                .build();

        mrpOutput.assignWithMrpExceptionReports(result.mrpExceptionReports());

        createMrpOutputPort.createMrpOutput(
                mrpOutput,
                Collections.emptyList(),
                Collections.emptyList(),
                result.mrpExceptionReports()
        );

        // Excel 파일 생성 및 S3 업로드
        String exceptionReportLink = null;

        if ("prod".equals(activeProfile)) {
            exceptionReportLink = createMrpReportFilePort.createAndUploadMrpExceptionReport(mrpOutput, result.mrpExceptionReports());
        }

        // 생성된 링크 업데이트
        mrpOutput.addMrpExceptionReportLink(exceptionReportLink);
    }
}
