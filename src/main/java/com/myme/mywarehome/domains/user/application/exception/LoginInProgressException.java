package com.myme.mywarehome.domains.user.application.exception;

import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class LoginInProgressException extends BusinessException {

    public LoginInProgressException() {
        super(ErrorCode.LOGIN_IN_PROGRESS);
    }
}
