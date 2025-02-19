package com.myme.mywarehome.domains.mrp.application.exception;

import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class MrpOutputNotFoundException extends BusinessException {
    public MrpOutputNotFoundException() {
        super(ErrorCode.MRP_OUTPUT_NOT_FOUND);
    }
}
