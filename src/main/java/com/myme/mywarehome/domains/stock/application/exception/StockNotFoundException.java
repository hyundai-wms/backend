package com.myme.mywarehome.domains.stock.application.exception;

import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class StockNotFoundException extends BusinessException {
    public StockNotFoundException() {
        super(ErrorCode.STOCK_NOT_FOUND);
    }
}