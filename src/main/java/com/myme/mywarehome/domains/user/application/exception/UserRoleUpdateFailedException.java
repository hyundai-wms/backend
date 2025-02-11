package com.myme.mywarehome.domains.user.application.exception;

import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class UserRoleUpdateFailedException extends BusinessException {
    public UserRoleUpdateFailedException() {
        super(ErrorCode.USER_ROLE_UPDATE_FAILED);
    }
}
