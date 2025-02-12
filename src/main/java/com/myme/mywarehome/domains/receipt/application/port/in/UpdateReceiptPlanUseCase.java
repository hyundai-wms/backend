package com.myme.mywarehome.domains.receipt.application.port.in;

import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.port.in.command.ReceiptPlanCommand;

public interface UpdateReceiptPlanUseCase {
    ReceiptPlan updateReceiptPlan(Long receiptPlanId, ReceiptPlanCommand command);
}
