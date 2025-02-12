package com.myme.mywarehome.domains.receipt.application.port.in;

import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.port.in.command.ReceiptPlanCommand;
import java.util.List;

public interface CreateReceiptPlanUseCase {
    ReceiptPlan createReceiptPlan(ReceiptPlanCommand command);
    List<ReceiptPlan> createReceiptPlanBulk(List<ReceiptPlanCommand> commandList);
}
