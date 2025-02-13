package com.myme.mywarehome.domains.receipt.application.exception;

import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class ReceiptNotFoundException extends BusinessException {
    public ReceiptNotFoundException() {
        super(ErrorCode.RECEIPT_NOT_FOUND);
    }
}
