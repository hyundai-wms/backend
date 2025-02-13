package com.myme.mywarehome.domains.stock.application.exception;

import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class NoAvailableBin extends BusinessException {
    public NoAvailableBin() {
        super(ErrorCode.NO_AVAILABLE_BIN);
    }
}
