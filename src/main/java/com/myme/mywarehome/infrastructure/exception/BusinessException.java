package com.myme.mywarehome.infrastructure.exception;

import com.myme.mywarehome.infrastructure.common.response.ErrorResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    private final List<ErrorResponse.FieldError> reason;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.reason = null;
    }

    public BusinessException(ErrorCode errorCode, List<ErrorResponse.FieldError> reason) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.reason = reason;
    }
}
