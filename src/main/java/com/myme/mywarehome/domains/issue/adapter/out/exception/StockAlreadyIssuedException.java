package com.myme.mywarehome.domains.issue.adapter.out.exception;

import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class StockAlreadyIssuedException extends BusinessException {
    public StockAlreadyIssuedException() {
        super(ErrorCode.STOCK_ALREADY_ISSUED);
    }
}