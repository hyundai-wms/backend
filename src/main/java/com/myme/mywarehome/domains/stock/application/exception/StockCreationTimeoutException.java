package com.myme.mywarehome.domains.stock.application.exception;

import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class StockCreationTimeoutException extends BusinessException {
    public StockCreationTimeoutException() {
        super(ErrorCode.STOCK_CREATION_TIMEOUT);
    }
}
