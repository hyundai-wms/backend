package com.myme.mywarehome.domains.user.adapter.out.exception;

import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class InvalidCurrentPasswordException extends BusinessException {
    public InvalidCurrentPasswordException() {
        super(ErrorCode.INVALID_CURRENT_PASSWORD);
    }
}
