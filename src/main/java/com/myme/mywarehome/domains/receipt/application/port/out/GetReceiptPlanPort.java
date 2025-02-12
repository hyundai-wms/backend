package com.myme.mywarehome.domains.receipt.application.port.out;

import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import java.util.Optional;

public interface GetReceiptPlanPort {
    Optional<ReceiptPlan> getReceiptPlanById(Long receiptPlanId);
}
