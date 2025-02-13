package com.myme.mywarehome.domains.receipt.application.exception;

import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class OutboundProductIdParseException extends BusinessException {

    public OutboundProductIdParseException() {
        super(ErrorCode.OUTBOUND_PRODUCT_ID_PARSE_FAILED);
    }
}
