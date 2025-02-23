package com.myme.mywarehome.domains.mrp.application.service;

import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecordItem;
import com.myme.mywarehome.domains.mrp.application.domain.MrpExceptionReport;
import com.myme.mywarehome.domains.mrp.application.domain.ProductionPlanningReport;
import com.myme.mywarehome.domains.mrp.application.domain.PurchaseOrderReport;
import com.myme.mywarehome.domains.mrp.application.port.in.MrpCalculatorUseCase;
import com.myme.mywarehome.domains.mrp.application.service.dto.MrpCalculateResultDto;
import com.myme.mywarehome.domains.mrp.application.service.dto.MrpContextDto;
import com.myme.mywarehome.domains.mrp.application.service.dto.MrpNodeDto;
import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.infrastructure.config.aspect.LogExecutionTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MrpCalculatorService implements MrpCalculatorUseCase {

    @Override
    @LogExecutionTime(value = "MRP Calculation")
    public MrpCalculateResultDto calculate(MrpNodeDto mrpNode, MrpContextDto context) {
        Product product = mrpNode.product();

        log.info("Starting MRP calculation for product: {}, Product ID: {}", product.getProductNumber(), product.getProductId());

        // 1. Y, Y', Y'' 계산
        long requiredCount = mrpNode.requiredPartsCount();  // Y (기본 필요 수량)
        double safetyIncludedCount = requiredCount * 1.1;     // Y' (안전재고 포함)
        double safetyStockCount = requiredCount * 0.1;        // 안전재고 수량

        log.info("Calculated requiredCount: {}, safetyIncludedCount: {}, safetyStockCount: {}",
                requiredCount, safetyIncludedCount, safetyStockCount);

        // Y'' (EA 조정된 수량) 계산
        int eachCount = product.getEachCount();
        long adjustedCount = (long) Math.ceil(safetyIncludedCount / eachCount) * eachCount;
        int safetyStock = (int) Math.ceil(safetyStockCount / eachCount);

        log.info("Adjusted order quantity (Y''): {} (eachCount: {}), safetyStock (EA): {}",
                adjustedCount, eachCount, safetyStock);

        // 2. 현재 재고 확인 및 필요 수량(N) 계산
        InventoryRecordItem inventoryItem = context.getInventoryRecord().get(product.getProductId());
        long currentStock = inventoryItem != null ? inventoryItem.getStockCount() : 0;
        long currentStockEA = currentStock * eachCount;     // P (현재 재고 EA)

        log.info("Current stock for product {}: {} units, which is {} EA",
                product.getProductNumber(), currentStock, currentStockEA);

        long neededCount = adjustedCount - currentStockEA;  // N (추가 필요 수량)
        log.info("Needed count (N): {} (AdjustedCount: {} - CurrentStockEA: {})",
                neededCount, adjustedCount, currentStockEA);

        if (neededCount <= 0) {
            log.info("No additional order required for product {}. NeededCount: {}", product.getProductNumber(), neededCount);
            return new MrpCalculateResultDto(0, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0);
        }

        long orderQuantity = neededCount / eachCount;       // X (발주/생산 수량)
        log.info("Calculated orderQuantity: {} (NeededCount: {} / eachCount: {})",
                orderQuantity, neededCount, eachCount);

        // 3. 리드타임 계산 및 날짜 검증
        long totalLeadTimeSeconds = neededCount * product.getLeadTime();
        int leadTimeDays = (int) Math.ceil(totalLeadTimeSeconds / (24.0 * 60 * 60));
        log.info("Total lead time (seconds): {}, Lead time (days): {}", totalLeadTimeSeconds, leadTimeDays);

        LocalDate startDate = context.getComputedDate().minusDays(leadTimeDays);
        LocalDate now = LocalDate.now();
        log.info("Computed startDate: {}, Today's date: {}", startDate, now);

        // 4. 보고서 생성
        List<PurchaseOrderReport> purchaseReports = new ArrayList<>();
        List<ProductionPlanningReport> productionReports = new ArrayList<>();
        List<MrpExceptionReport> exceptionReports = new ArrayList<>();

        // 5-1. 문제 검사 : 리드타임 계산 시 납기일 불가능
        if (startDate.isBefore(now)) {
            MrpExceptionReport mrpExceptionReport = MrpExceptionReport.builder()
                    .exceptionType("LEAD_TIME_VIOLATION")
                    .exceptionMessage(String.format(
                            "제품 %s는 %d일의 리드타임(LT)이 필요하며, %s부터 시작됩니다.",
                            product.getProductNumber(), leadTimeDays, startDate))
                    .build();
            exceptionReports.add(mrpExceptionReport);
            log.warn("Lead time violation for product {}: {}", product.getProductNumber(), mrpExceptionReport.getExceptionMessage());
        }

        // 5-2. 문제 검사 : Bin 용량 초과
        long totalBinsNeeded = (currentStock + orderQuantity) / 10;
        int availableBins = product.getBayList().size() * 10;
        log.info("Total bins needed: {}, Available bins: {}", totalBinsNeeded, availableBins);

        if (totalBinsNeeded > availableBins) {
            MrpExceptionReport mrpExceptionReport = MrpExceptionReport.builder()
                    .exceptionType("BIN_CAPACITY_EXCEEDED")
                    .exceptionMessage(String.format(
                            "제품 %s는 총 %d개의 Bin이 필요하지만 현재 %d개의 Bin만 사용 가능합니다.",
                            product.getProductNumber(), totalBinsNeeded, availableBins))
                    .build();
            exceptionReports.add(mrpExceptionReport);
            log.warn("Bin capacity exceeded for product {}: {}", product.getProductNumber(), mrpExceptionReport.getExceptionMessage());
        }

        // 6. 보고서 생성 분기 : 생산 vs 발주
        if (!product.getCompany().getIsVendor()) {
            // 엔진이거나 자체 생산 부품인 경우
            ProductionPlanningReport productionReport = ProductionPlanningReport.builder()
                    .productionPlanningDate(startDate)
                    .receiptPlanDate(context.getComputedDate())
                    .product(product)
                    .quantity(orderQuantity)
                    .safeItemCount(safetyStock)
                    .build();
            productionReports.add(productionReport);
            context.updateComputedDate(startDate);
            log.info("Production report generated for product {}: Quantity: {}, Production date: {}",
                    product.getProductNumber(), orderQuantity, startDate);
        } else {
            // 외주 부품인 경우
            PurchaseOrderReport purchaseReport = PurchaseOrderReport.builder()
                    .purchaseOrderDate(startDate)
                    .receiptPlanDate(context.getComputedDate())
                    .product(product)
                    .quantity(orderQuantity)
                    .safeItemCount(safetyStock)
                    .build();
            purchaseReports.add(purchaseReport);
            log.info("Purchase order report generated for product {}: Quantity: {}, Purchase order date: {}",
                    product.getProductNumber(), orderQuantity, startDate);
        }

        MrpCalculateResultDto result = new MrpCalculateResultDto(
                orderQuantity,
                purchaseReports,
                productionReports,
                exceptionReports,
                product.getCompany().getIsVendor() ? leadTimeDays : 0
        );
        log.info("MRP calculation result for product {}: OrderQuantity: {}, Exceptions: {}",
                product.getProductNumber(), orderQuantity, exceptionReports.size());
        return result;
    }
}
