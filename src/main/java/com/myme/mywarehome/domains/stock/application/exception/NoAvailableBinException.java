package com.myme.mywarehome.domains.stock.application.exception;

import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class NoAvailableBinException extends BusinessException {
    public NoAvailableBinException() {
        super(ErrorCode.NO_AVAILABLE_BIN);
    }
}
