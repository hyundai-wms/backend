package com.myme.mywarehome.infrastructure.exception;

import lombok.Getter;

@Getter
public class AsyncException extends RuntimeException {

    private final AsyncErrorCode errorCode;
    private final String eventId;
    private final Object eventData;

    public AsyncException(AsyncErrorCode errorCode, String eventId, Object eventData) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.eventId = eventId;
        this.eventData = eventData;
    }

    public AsyncException(AsyncErrorCode errorCode, String eventId, Object eventData, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.eventId = eventId;
        this.eventData = eventData;
    }
}
