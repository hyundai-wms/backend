package com.myme.mywarehome.domains.mrp.application.port.in.event;

import com.myme.mywarehome.domains.mrp.application.domain.ProductionPlanningReport;
import com.myme.mywarehome.domains.mrp.application.domain.PurchaseOrderReport;
import com.myme.mywarehome.domains.product.application.port.in.command.UpdateSafeItemCountCommand;

import java.util.ArrayList;
import java.util.List;

public record UpdateSafetyStockFromMrpEvent(
        List<PurchaseOrderReport> purchaseOrderReports,
        List<ProductionPlanningReport> productionPlanningReports
) {
    public List<UpdateSafeItemCountCommand> toCommands() {
        List<UpdateSafeItemCountCommand> commands = new ArrayList<>();

        // Purchase Orders에서 Command 생성
        commands.addAll(purchaseOrderReports.stream()
                .map(report -> new UpdateSafeItemCountCommand(
                        report.getProduct().getProductNumber(),
                        report.getSafeItemCount()))
                .toList());

        // Production Plans에서 Command 생성
        commands.addAll(productionPlanningReports.stream()
                .map(report -> new UpdateSafeItemCountCommand(
                        report.getProduct().getProductNumber(),
                        report.getSafeItemCount()))
                .toList());

        return commands;
    }
}
