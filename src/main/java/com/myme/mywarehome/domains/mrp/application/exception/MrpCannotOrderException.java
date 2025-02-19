package com.myme.mywarehome.domains.mrp.application.exception;

import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class MrpCannotOrderException extends BusinessException {
    public MrpCannotOrderException() {
        super(ErrorCode.MRP_CANNOT_ORDER);
    }
}
