package com.myme.mywarehome.domains.mrp.application.exception;

import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class EngineNotFoundException extends BusinessException {
    public EngineNotFoundException() {
        super(ErrorCode.ENGINE_NOT_FOUND);
    }
}