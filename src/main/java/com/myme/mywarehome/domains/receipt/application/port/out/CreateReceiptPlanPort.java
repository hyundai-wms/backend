package com.myme.mywarehome.domains.receipt.application.port.out;

import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import java.util.List;

public interface CreateReceiptPlanPort {
    ReceiptPlan create(ReceiptPlan receiptPlan);
    List<ReceiptPlan> createBulk(List<ReceiptPlan> receiptPlanList);
}