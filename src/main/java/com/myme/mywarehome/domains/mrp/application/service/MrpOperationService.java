package com.myme.mywarehome.domains.mrp.application.service;

import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecordItem;
import com.myme.mywarehome.domains.mrp.application.port.in.*;
import com.myme.mywarehome.domains.mrp.application.port.in.command.MrpInputCommand;
import com.myme.mywarehome.domains.mrp.application.port.out.GetInventoryRecordPort;
import com.myme.mywarehome.domains.mrp.application.service.dto.*;
import com.myme.mywarehome.infrastructure.config.aspect.LogExecutionTime;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MrpOperationService implements MrpOperationUseCase {
    private final CreateInventoryRecordUseCase createInventoryRecordUseCase;
    private final GetInventoryRecordPort getInventoryRecordPort;
    private final MrpBomTreeUseCase mrpBomTreeUseCase;
    private final MrpBomTreeTraversalUseCase mrpBomTreeTraversalUseCase;
    private final MrpOutputUseCase mrpOutputUseCase;

    @Override
    @LogExecutionTime(value = "MRP Operation")
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public void run(MrpInputCommand command) {
        String operationId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        try {
            setupLoggingContext(operationId, command);

            // 1. 재고 기록 생성 및 조회
            log.info("MRP operation started - Creating inventory snapshot");
            long inventoryStartTime = System.currentTimeMillis();
            createInventoryRecordUseCase.createInventoryRecord();
            List<InventoryRecordItem> inventoryItems = getInventoryRecordPort.findRecentInventoryRecord();
            logInventoryMetrics(inventoryItems, System.currentTimeMillis() - inventoryStartTime);

            // 2. MRP Context 생성
            MrpContextDto context = createMrpContext(command, inventoryItems);
            log.info("MRP context created - Computation date: {}, Total inventory items: {}",
                    context.getComputedDate(), context.getInventoryRecord().size());

            // 3. BOM Tree 생성
            log.info("Creating unified BOM tree structure");
            long bomStartTime = System.currentTimeMillis();
            UnifiedBomDataDto unifiedBomData = mrpBomTreeUseCase.createUnifiedBomTree(command);
            logBomTreeMetrics(unifiedBomData, System.currentTimeMillis() - bomStartTime);

            // 4. MRP 계산 실행
            log.info("Starting MRP calculation traversal");
            long calculationStartTime = System.currentTimeMillis();
            MrpCalculateResultDto result = mrpBomTreeTraversalUseCase.traverse(command, unifiedBomData, context);
            logCalculationResults(result, System.currentTimeMillis() - calculationStartTime);

            // 5. 결과 저장 (DB 저장 성능 측정)
            log.info("Saving MRP calculation results started");
            long saveStartTime = System.currentTimeMillis();
            mrpOutputUseCase.saveResults(command, result);
            long saveDuration = System.currentTimeMillis() - saveStartTime;
            log.info("DB save completed - Duration: {}ms", saveDuration);

            // 6. 최종 실행 결과 요약
            logOperationSummary(startTime, System.currentTimeMillis(), command, result);

        } catch (Exception e) {
            log.error("MRP operation failed - Operation ID: {}, Error: {}", operationId, e.getMessage(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    private void setupLoggingContext(String operationId, MrpInputCommand command) {
        MDC.put("operationId", operationId);
        MDC.put("dueDate", command.dueDate().toString());
        MDC.put("engineCounts", formatEngineCounts(command.engineCountMap()));
        MDC.put("startTime", LocalDateTime.now().toString());
    }

    private void logInventoryMetrics(List<InventoryRecordItem> items, long duration) {
        Map<Boolean, Long> stockStatusCount = items.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getStockCount() > 0,
                        Collectors.counting()
                ));

        log.info("Inventory snapshot completed - Duration: {}ms, Total items: {}, Items with stock: {}, Items without stock: {}",
                duration, items.size(),
                stockStatusCount.getOrDefault(true, 0L),
                stockStatusCount.getOrDefault(false, 0L));
    }

    private MrpContextDto createMrpContext(MrpInputCommand command, List<InventoryRecordItem> items) {
        return MrpContextDto.builder()
                .inventoryRecord(items.stream()
                        .collect(Collectors.toMap(
                                item -> item.getProduct().getProductId(),
                                item -> item
                        )))
                .computedDate(command.dueDate().minusDays(1))
                .build();
    }

    private void logBomTreeMetrics(UnifiedBomDataDto bomData, long duration) {
        log.info("BOM tree creation completed - Duration: {}ms, Virtual root: {}, Tree size: {}, Product mapping count: {}",
                duration,
                bomData.virtualRoot().getProductNumber(),
                bomData.unifiedBomTree().size(),
                bomData.bomTreeMap().size());
    }

    private void logCalculationResults(MrpCalculateResultDto result, long duration) {
        log.info("MRP calculation completed - Duration: {}ms, Purchase orders: {}, Production orders: {}, Exceptions: {}, Lead time days: {}",
                duration,
                result.purchaseOrderReports().size(),
                result.productionPlanningReports().size(),
                result.mrpExceptionReports().size(),
                result.leadTimeDays());

        if (result.hasExceptions()) {
            log.warn("MRP calculation produced exceptions - Count: {}, Details: {}",
                    result.mrpExceptionReports().size(),
                    result.mrpExceptionReports());
        }
    }

    private void logOperationSummary(long startTime, long endTime, MrpInputCommand command, MrpCalculateResultDto result) {
        long totalDuration = endTime - startTime;
        log.info("MRP operation completed successfully - Total duration: {}ms, Due date: {}, Total planned items: {}, Required parts: {}, Exceptions: {}",
                totalDuration,
                command.dueDate(),
                result.purchaseOrderReports().size() + result.productionPlanningReports().size(),
                result.nextRequiredPartsCount(),
                result.mrpExceptionReports().size());
    }

    private String formatEngineCounts(Map<String, Integer> engineCounts) {
        return engineCounts.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(", "));
    }
}
