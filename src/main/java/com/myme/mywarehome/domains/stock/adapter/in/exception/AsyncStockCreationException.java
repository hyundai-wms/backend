package com.myme.mywarehome.domains.stock.adapter.in.exception;

import com.myme.mywarehome.infrastructure.exception.AsyncErrorCode;
import com.myme.mywarehome.infrastructure.exception.AsyncException;

public class AsyncStockCreationException extends AsyncException {

    public AsyncStockCreationException(String eventId, Object eventData) {
        super(AsyncErrorCode.STOCK_CREATION_FAILED, eventId, eventData);
    }

    public AsyncStockCreationException(String eventId, Object eventData, Throwable cause) {
        super(AsyncErrorCode.STOCK_CREATION_FAILED, eventId, eventData, cause);
    }
}
