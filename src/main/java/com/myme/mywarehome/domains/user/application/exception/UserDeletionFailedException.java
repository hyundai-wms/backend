package com.myme.mywarehome.domains.user.application.exception;

import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class UserDeletionFailedException extends BusinessException {
    public UserDeletionFailedException() {
        super(ErrorCode.USER_DELETION_FAILED);
    }
}
