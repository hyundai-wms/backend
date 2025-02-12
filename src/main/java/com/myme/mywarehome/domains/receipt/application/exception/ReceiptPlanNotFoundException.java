package com.myme.mywarehome.domains.receipt.application.exception;

import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class ReceiptPlanNotFoundException extends BusinessException {
    public ReceiptPlanNotFoundException() {
        super(ErrorCode.RECEIPT_PLAN_NOT_FOUND);
    }
}
