package com.myme.mywarehome.domains.mrp.application.service;

import com.myme.mywarehome.domains.issue.application.port.in.command.IssuePlanCommand;
import com.myme.mywarehome.domains.mrp.application.domain.BomTree;
import com.myme.mywarehome.domains.mrp.application.domain.MrpOutput;
import com.myme.mywarehome.domains.mrp.application.domain.ProductionPlanningReport;
import com.myme.mywarehome.domains.mrp.application.domain.PurchaseOrderReport;
import com.myme.mywarehome.domains.mrp.application.exception.MrpCannotOrderException;
import com.myme.mywarehome.domains.mrp.application.exception.MrpOutputNotFoundException;
import com.myme.mywarehome.domains.mrp.application.port.in.MrpOrderUseCase;
import com.myme.mywarehome.domains.mrp.application.port.in.event.CreatePlanFromMrpEvent;
import com.myme.mywarehome.domains.mrp.application.port.in.event.UpdateSafetyStockFromMrpEvent;
import com.myme.mywarehome.domains.mrp.application.port.out.GetBomTreePort;
import com.myme.mywarehome.domains.mrp.application.port.out.GetMrpOutputPort;
import com.myme.mywarehome.domains.mrp.application.port.out.UpdateMrpOutputPort;
import com.myme.mywarehome.domains.receipt.application.port.in.command.ReceiptPlanCommand;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MrpOrderService implements MrpOrderUseCase {
    private final GetMrpOutputPort getMrpOutputPort;
    private final GetBomTreePort getBomTreePort;
    private final UpdateMrpOutputPort updateMrpOutputPort;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public void run(Long mrpOutputId) {
        MrpOutput mrpOutput = getMrpOutputPort.getMrpOutputByMrpOutputId(mrpOutputId)
                .orElseThrow(MrpOutputNotFoundException::new);

        // 1. MRP 보고서가 canOrdered인 경우만 생성 가능
        if(!mrpOutput.getCanOrder()) {
            throw new MrpCannotOrderException();
        }

        // 2. 생성된 MRP 보고서를 바탕으로 입고 계획 리스트 생성
        List<ReceiptPlanCommand> receiptPlanCommands = new ArrayList<>();

        // PurchaseOrderReport로부터 입고 계획 생성
        receiptPlanCommands.addAll(createReceiptPlanCommandsFromPurchaseOrders(mrpOutput.getPurchaseOrderReportList()));
        // ProductionPlanningReport로부터 입고 계획 생성
        receiptPlanCommands.addAll(createReceiptPlanCommandsFromProductionPlans(mrpOutput.getProductionPlanningReportList()));

        // 3. 출고 계획 리스트 생성
        List<IssuePlanCommand> issuePlanCommands = new ArrayList<>();

        // ProductionPlanningReport의 하위 부품들에 대한 출고 계획 생성
        issuePlanCommands.addAll(createIssuePlanCommandsFromProductionPlans(mrpOutput.getProductionPlanningReportList()));
        // 엔진 출고 계획 생성
        issuePlanCommands.addAll(createEngineIssuePlanCommands(mrpOutput));

        // 4. 입고/출고 계획 생성 Bulk 이벤트 발행
        eventPublisher.publishEvent(new CreatePlanFromMrpEvent(receiptPlanCommands, issuePlanCommands));

        // 5. 안전 재고 추가 이벤트 발행
        eventPublisher.publishEvent(new UpdateSafetyStockFromMrpEvent(
                mrpOutput.getPurchaseOrderReportList(),
                mrpOutput.getProductionPlanningReportList()
        ));

        // 6. 생성 완료 시 isOrdered를 true로 변경 및 생산/발주 지시 성공
        mrpOutput.orderSuccess();
        updateMrpOutputPort.orderSuccess(mrpOutput);
    }

    private List<ReceiptPlanCommand> createReceiptPlanCommandsFromPurchaseOrders(
            List<PurchaseOrderReport> purchaseOrderReports) {
        return purchaseOrderReports.stream()
                .map(report -> new ReceiptPlanCommand(report.getProduct().getProductNumber(), report.getQuantity().intValue(), report.getReceiptPlanDate()))
                .toList();
    }

    private List<ReceiptPlanCommand> createReceiptPlanCommandsFromProductionPlans(
            List<ProductionPlanningReport> productionPlanningReports) {
        return productionPlanningReports.stream()
                .map(report -> new ReceiptPlanCommand(report.getProduct().getProductNumber(), report.getQuantity().intValue(), report.getReceiptPlanDate()))
                .toList();
    }

    private List<IssuePlanCommand> createIssuePlanCommandsFromProductionPlans(
            List<ProductionPlanningReport> productionPlanningReports) {
        List<IssuePlanCommand> issuePlanCommands = new ArrayList<>();

        for (ProductionPlanningReport report : productionPlanningReports) {
            // 각 ProductionPlanningReport의 product에 대해 BomTree 조회
            List<BomTree> childBomTrees = getBomTreePort.findAllByParentNumber(
                    report.getProduct().getProductNumber());

            // 하위 부품들에 대한 출고 계획 생성
            List<IssuePlanCommand> childIssuePlans = childBomTrees.stream()
                    .map(bomTree -> new IssuePlanCommand(bomTree.getChildProduct().getProductNumber(), (int) (bomTree.getChildCompositionRatio() * report.getQuantity()), report.getProductionPlanningDate()))
                    .toList();

            issuePlanCommands.addAll(childIssuePlans);
        }

        return issuePlanCommands;
    }

    private List<IssuePlanCommand> createEngineIssuePlanCommands(MrpOutput mrpOutput) {
        List<IssuePlanCommand> engineIssuePlans = new ArrayList<>();

        // 각 엔진별로 IssuePlanCommand 생성
        // Kappa 엔진
        if (mrpOutput.getKappaCount() != null && mrpOutput.getKappaCount() > 0) {
            engineIssuePlans.add(new IssuePlanCommand("10000-03P00", mrpOutput.getKappaCount(), mrpOutput.getDueDate()));
        }

        // Gamma 엔진
        if (mrpOutput.getGammaCount() != null && mrpOutput.getGammaCount() > 0) {
            engineIssuePlans.add(new IssuePlanCommand("10000-04P00", mrpOutput.getKappaCount(), mrpOutput.getDueDate()));
        }

        // Nu 엔진
        if (mrpOutput.getNuCount() != null && mrpOutput.getNuCount() > 0) {
            engineIssuePlans.add(new IssuePlanCommand("10000-05P00", mrpOutput.getKappaCount(), mrpOutput.getDueDate()));
        }

        // Theta 엔진
        if (mrpOutput.getThetaCount() != null && mrpOutput.getThetaCount() > 0) {
            engineIssuePlans.add(new IssuePlanCommand("10000-06P00", mrpOutput.getKappaCount(), mrpOutput.getDueDate()));
        }

        return engineIssuePlans;
    }

}
