package com.myme.mywarehome.domains.receipt.application.port.in;

import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.port.in.command.CreateReceiptPlanCommand;

public interface CreateReceiptPlanUseCase {
    ReceiptPlan createReceiptPlan(CreateReceiptPlanCommand command);
}
