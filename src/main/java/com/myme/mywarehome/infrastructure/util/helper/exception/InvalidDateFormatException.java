package com.myme.mywarehome.infrastructure.util.helper.exception;

import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class InvalidDateFormatException extends BusinessException {
    public InvalidDateFormatException() {
        super(ErrorCode.INVALID_DATE_FORMAT);
    }
}
