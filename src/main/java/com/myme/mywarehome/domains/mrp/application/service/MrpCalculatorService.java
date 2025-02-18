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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MrpCalculatorService implements MrpCalculatorUseCase {

    // inventoryRecord : 재고기록철 맵
    // computedDate : 이번에 계산된 날짜
    // mrpNode : 어떤 물품과 그 때 필요한 수량

    // 1. 해당하는 트리의 노드를 바탕으로 계산

    // 2. 안전재고 계산
    //   - 안전재고는 이번에 들어온 수량 * 0.1
    //   - 이것도 저장해야 하네

    // 3. 발주/생산/예외 보고.. 생성

    // OUTPUT
    // 1. 발주 예정 보고서
    //   - 발주 일자(가상의 날짜)
    //   - 입고 되어 들어오는 날짜(입고 예정일)
    //   - 발주 물품
    //   - 발주 수량(EA인지 SET인지 계산 잘하기)
    // 2. 생산 지시 보고서
    //   - 생산 일자(입고 예정일)
    //   - 생산을 위해 하위 부품이 출고 되어야 하는 날짜(출고 예정일)
    //   - 생산 물품
    //   - 하위 부품 리스트 (조회로 가져오기)
    //   - 생산 물품 수량
    // 3. 예외 보고서
    //   - 만약, 납기일 내로 생산하지 못한다면 에러
    //   - 만약, Bin 크기 이상으로 데이터가 입고되어야 하면 에러
    //   - 현재 재고가 안전재고 미만인 품목(WMS)
    @Override
    public MrpCalculateResultDto calculate(MrpNodeDto mrpNode, MrpContextDto context) {
        Product product = mrpNode.product();

        // 1. Y, Y', Y'' 계산
        long requiredCount = mrpNode.requiredPartsCount();  // Y (기본 필요 수량)
        double safetyIncludedCount = requiredCount * 1.1;  // Y' (안전재고 포함)
        int safetyStock = (int) (requiredCount * 0.1);     // 안전재고 수량

        // Y'' (EA 조정된 수량) 계산
        int eachCount = product.getEachCount();
        long adjustedCount = (long) Math.ceil(safetyIncludedCount / eachCount) * eachCount;

        // 2. 현재 재고 확인 및 필요 수량(N) 계산
        InventoryRecordItem inventoryItem = context.getInventoryRecord().get(product.getProductId());
        long currentStock = inventoryItem != null ? inventoryItem.getStockCount() : 0;
        long currentStockEA = currentStock * eachCount;     // P (현재 재고 EA)

        long neededCount = adjustedCount - currentStockEA;  // N (추가 필요 수량)
        if (neededCount <= 0) {
            return new MrpCalculateResultDto(0, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }

        long orderQuantity = neededCount / eachCount;       // X (발주/생산 수량)

        // 3. 리드타임 계산 및 날짜 검증
        long totalLeadTimeSeconds = neededCount * product.getLeadTime();
        int leadTimeDays = (int) Math.ceil(totalLeadTimeSeconds / (24.0 * 60 * 60));

        LocalDate startDate = context.getComputedDate().minusDays(leadTimeDays);
        LocalDate now = LocalDate.now();

        if (startDate.isBefore(now)) {
            MrpExceptionReport exceptionReport = MrpExceptionReport.builder()
                    .exceptionType("LEAD_TIME_VIOLATION")
                    .exceptionMessage(String.format(
                            "제품 %s는 %d일의 리드타임(LT)이 필요하며, %s부터 시작됩니다.",
                            product.getProductNumber(), leadTimeDays, startDate))
                    .build();
            return MrpCalculateResultDto.withException(exceptionReport);
        }

        // 4. Bin 용량 검증
        long totalBinsNeeded = (currentStock + orderQuantity) / 10;
        int availableBins = product.getBayList().size() * 10;

        if (totalBinsNeeded > availableBins) {
            MrpExceptionReport exceptionReport = MrpExceptionReport.builder()
                    .exceptionType("BIN_CAPACITY_EXCEEDED")
                    .exceptionMessage(String.format(
                            "제품 %s는 총 %d개의 Bin이 필요하지만 현재 %d개의 Bin만 사용 가능합니다.",
                            product.getProductNumber(), totalBinsNeeded, availableBins))
                    .build();
            return MrpCalculateResultDto.withException(exceptionReport);
        }

        // 5. 보고서 생성
        List<PurchaseOrderReport> purchaseReports = new ArrayList<>();
        List<ProductionPlanningReport> productionReports = new ArrayList<>();

        if (!product.getCompany().getIsVendor()) {
            // 엔진이거나 자체 생산 부품인 경우
            ProductionPlanningReport productionReport = ProductionPlanningReport.builder()
                    .productionPlanningDate(startDate)
                    .issuePlanDate(context.getComputedDate())
                    .product(product)
                    .quantity(orderQuantity)
                    .safeItemCount(safetyStock)
                    .build();
            productionReports.add(productionReport);
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
        }

        // 계산된 날짜 업데이트
        context.updateComputedDate(startDate);

        return new MrpCalculateResultDto(
                orderQuantity,
                purchaseReports,
                productionReports,
                new ArrayList<>()
        );
    }
}
