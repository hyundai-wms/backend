package com.myme.mywarehome.domains.receipt.application.port.out;

import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;

public interface CreateReceiptPlanPort {
    ReceiptPlan create(ReceiptPlan receiptPlan);
}