package com.myme.mywarehome.domains.issue.adapter.out.exception;

import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class StockAssignTimeoutException extends BusinessException {
    public StockAssignTimeoutException() {
        super(ErrorCode.STOCK_ASSIGN_TIMEOUT);
    }
}