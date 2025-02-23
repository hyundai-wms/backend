package com.myme.mywarehome.domains.mrp.application.exception;

import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class MrpCalculationInProgressException extends BusinessException {

    public MrpCalculationInProgressException() {
        super(ErrorCode.MRP_CALCULATION_IN_PROGRESS);
    }
}
