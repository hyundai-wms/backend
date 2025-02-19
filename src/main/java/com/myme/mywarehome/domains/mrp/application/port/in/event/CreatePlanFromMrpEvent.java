package com.myme.mywarehome.domains.mrp.application.port.in.event;

import com.myme.mywarehome.domains.issue.application.port.in.command.IssuePlanCommand;
import com.myme.mywarehome.domains.receipt.application.port.in.command.ReceiptPlanCommand;

import java.util.List;

public record CreatePlanFromMrpEvent(
        List<ReceiptPlanCommand> receiptPlanCommands,
        List<IssuePlanCommand> issuePlanCommands
) {
}
