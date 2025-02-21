package com.myme.mywarehome.domains.user.application.exception;

import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class UnexpectedLoginException extends BusinessException {

    public UnexpectedLoginException() {
        super(ErrorCode.UNEXPECTED_LOGIN);
    }
}
