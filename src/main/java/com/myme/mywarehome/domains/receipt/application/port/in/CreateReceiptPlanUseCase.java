package com.myme.mywarehome.domains.receipt.application.port.in;

import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.port.in.command.CreateReceiptPlanCommand;
import java.util.List;

public interface CreateReceiptPlanUseCase {
    ReceiptPlan createReceiptPlan(CreateReceiptPlanCommand command);
    List<ReceiptPlan> createReceiptPlanBulk(List<CreateReceiptPlanCommand> commandList);
}
