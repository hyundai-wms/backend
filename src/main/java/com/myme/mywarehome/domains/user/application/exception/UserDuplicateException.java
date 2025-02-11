package com.myme.mywarehome.domains.user.application.exception;

import com.myme.mywarehome.infrastructure.common.response.ErrorResponse;
import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

import java.util.ArrayList;
import java.util.List;

public class UserDuplicateException extends BusinessException {
    public UserDuplicateException() {
        super(ErrorCode.USER_DUPLICATED);
    }

    public UserDuplicateException(String duplicatedId, String duplicatedPhone) {
        super(ErrorCode.USER_DUPLICATED, createFieldErrors(duplicatedId, duplicatedPhone));
    }

    private static List<ErrorResponse.FieldError> createFieldErrors(String duplicatedId, String duplicatedPhone) {
        List<ErrorResponse.FieldError> fieldErrors = new ArrayList<>();

        if (duplicatedId != null) {
            fieldErrors.add(new ErrorResponse.FieldError("id", duplicatedId, "이미 등록된 ID입니다."));
        }

        if (duplicatedPhone != null) {
            fieldErrors.add(new ErrorResponse.FieldError("phoneNumber", duplicatedPhone, "이미 등록된 전화번호입니다."));
        }

        return fieldErrors;
    }
}
