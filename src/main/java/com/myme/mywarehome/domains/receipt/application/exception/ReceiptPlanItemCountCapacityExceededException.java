package com.myme.mywarehome.domains.receipt.application.exception;

import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class ReceiptPlanItemCountCapacityExceededException extends BusinessException {

    public ReceiptPlanItemCountCapacityExceededException() {
        super(ErrorCode.RECEIPT_PLAN_ITEM_CAPACITY_EXCEEDED);
    }
}
