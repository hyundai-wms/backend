package com.myme.mywarehome.domains.receipt.application.exception;

import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class DuplicatedOutboundProductException extends BusinessException {

    public DuplicatedOutboundProductException() {
        super(ErrorCode.DUPLICATED_OUTBOUND_PRODUCT);
    }
}
