package com.myme.mywarehome.domains.receipt.application.port.out;

public interface GetReturnPort {
    long countByReceiptPlanId(Long receiptPlanId);
}
