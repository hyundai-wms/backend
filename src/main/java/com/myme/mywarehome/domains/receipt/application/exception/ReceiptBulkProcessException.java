package com.myme.mywarehome.domains.receipt.application.exception;

import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class ReceiptBulkProcessException extends BusinessException {
    public ReceiptBulkProcessException() {
        super(ErrorCode.RECEIPT_BULK_PROCESS_FAILED);
    }
}
