package com.myme.mywarehome.infrastructure.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Component
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        if (ex instanceof AsyncException asyncException) {
            log.error(
                    "Async operation failed. ErrorCode: {}, EventId: {}, Message: {}, EventData: {}",
                    asyncException.getErrorCode().getCode(),
                    asyncException.getEventId(),
                    asyncException.getMessage(),
                    asyncException.getEventData(),
                    ex
            );
        } else {
            log.error(
                    "Unexpected async operation error in method: {}",
                    method.getName(),
                    ex
            );
        }
    }
}
