package com.myme.mywarehome.domains.user.application.domain.exception;

import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class LoginFailedException extends BusinessException {
    public LoginFailedException() {
        super(ErrorCode.LOGIN_FAILED);
    }
}
