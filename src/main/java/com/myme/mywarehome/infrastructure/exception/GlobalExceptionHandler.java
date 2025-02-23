package com.myme.mywarehome.infrastructure.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.myme.mywarehome.infrastructure.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 비즈니스 예외
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        String requestId = MDC.get("requestId");
        ErrorCode errorCode = e.getErrorCode();
        List<ErrorResponse.FieldError> fieldErrors = e.getReason();

        log.error(
                "[ERROR] type=BusinessException, requestId={}, errorCode={}, status={}, message={}, fieldErrors={}",
                requestId, errorCode.name(), errorCode.getStatus(), e.getMessage(), fieldErrors);

        ErrorResponse response;
        if (fieldErrors == null || fieldErrors.isEmpty()) {
            response = ErrorResponse.of(errorCode);
        } else {
            response = ErrorResponse.of(errorCode, fieldErrors);
        }

        return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
    }

    // 2. Valid 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e) {
        String requestId = MDC.get("requestId");
        List<ErrorResponse.FieldError> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorResponse.FieldError(
                        error.getField(),
                        error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                        error.getDefaultMessage()))
                .toList();

        log.error("[ERROR] type=ValidationError, requestId={}, fieldErrors={}",
                requestId, fieldErrors);

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, fieldErrors));
    }

    // 3. 경로 변수 타입 불일치
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException e) {
        String requestId = MDC.get("requestId");
        String fieldName = e.getName();
        String value = e.getValue() == null ? "" : e.getValue().toString();
        String requiredType = Objects.requireNonNull(e.getRequiredType()).getSimpleName();

        log.error("[ERROR] type=TypeMismatch, requestId={}, field={}, value={}, requiredType={}",
                requestId, fieldName, value, requiredType);

        List<ErrorResponse.FieldError> fieldErrors = List.of(
                new ErrorResponse.FieldError(
                        e.getName(),
                        e.getValue() == null ? "" : e.getValue().toString(),
                        String.format("%s 타입으로 변환할 수 없습니다.", Objects.requireNonNull(
                                e.getRequiredType()).getSimpleName())
                )
        );

        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_TYPE_VALUE, fieldErrors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 4. 경로 변수 없음
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        String requestId = MDC.get("requestId");
        String errorMessage;
        String fieldName = "requestBody";

        if (e.getMessage().contains("Required request body is missing")) {
            errorMessage = "요청 본문이 필요합니다.";
        } else {
            Throwable cause = e.getCause();
            if (cause instanceof JsonParseException jpe) {
                fieldName = "JSON parsing error";
                errorMessage = String.format("위치 %s에서 JSON 파싱 오류가 발생했습니다: %s",
                        jpe.getLocation(), jpe.getOriginalMessage());
            } else if (cause instanceof JsonMappingException jme) {
                fieldName =
                        jme.getPath().isEmpty() ? "Unknown" : jme.getPath().get(0).getFieldName();
                errorMessage = String.format("필드 '%s'의 값이 잘못되었습니다: %s",
                        fieldName, jme.getOriginalMessage());
            } else {
                errorMessage = "요청 본문의 형식이 잘못되었습니다.";
            }
        }

        log.error(
                "[ERROR] type=InvalidRequestBody, requestId={}, field={}, message={}, stackTrace={}",
                requestId, fieldName, errorMessage, ExceptionUtils.getStackTrace(e));

        List<ErrorResponse.FieldError> fieldErrors = List.of(
                new ErrorResponse.FieldError(
                        e.getParameterName(),
                        "",
                        "필수 파라미터 누락"
                )
        );

        ErrorResponse response = ErrorResponse.of(ErrorCode.MISSING_INPUT_VALUE, fieldErrors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 5. HTTP 메소드 불일치
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException e) {
        String requestId = MDC.get("requestId");
        log.error("[ERROR] type=MethodNotAllowed, requestId={}, method={}, supportedMethods={}",
                requestId, e.getMethod(), e.getSupportedHttpMethods());

        ErrorResponse response = ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED);
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // 6. 컨트롤러를 찾을 수 없음
    @ExceptionHandler(NoResourceFoundException.class)
    protected ResponseEntity<ErrorResponse> handleNoResourceFoundException(
            NoResourceFoundException e) {
        String requestId = MDC.get("requestId");
        log.error("[ERROR] type=RouteNotFound, requestId={}, path={}",
                requestId, e.getResourcePath());

        ErrorResponse response = ErrorResponse.of(ErrorCode.ROUTE_NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 7. Request Body 누락 또는 형식 오류
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e) {
        String requestId = MDC.get("requestId");
        String errorMessage;
        String fieldName = "requestBody";

        if (e.getMessage().contains("Required request body is missing")) {
            errorMessage = "요청 본문이 필요합니다.";
            log.error("[ERROR] type=RequestBodyMissing, requestId={}, message={}",
                    requestId, errorMessage);
        } else {
            Throwable cause = e.getCause();
            if (cause instanceof JsonParseException jpe) {
                fieldName = "JSON parsing error";
                errorMessage = String.format("위치 %s에서 JSON 파싱 오류가 발생했습니다: %s",
                        jpe.getLocation(), jpe.getOriginalMessage());
                log.error(
                        "[ERROR] type=JsonParseError, requestId={}, location={}, message={}, stackTrace={}",
                        requestId, jpe.getLocation(), jpe.getOriginalMessage(),
                        ExceptionUtils.getStackTrace(jpe));
            } else if (cause instanceof JsonMappingException jme) {
                fieldName =
                        jme.getPath().isEmpty() ? "Unknown" : jme.getPath().get(0).getFieldName();
                errorMessage = String.format("필드 '%s'의 값이 잘못되었습니다: %s",
                        fieldName, jme.getOriginalMessage());
                log.error(
                        "[ERROR] type=JsonMappingError, requestId={}, field={}, message={}, stackTrace={}",
                        requestId, fieldName, jme.getOriginalMessage(),
                        ExceptionUtils.getStackTrace(jme));
            } else {
                errorMessage = "요청 본문의 형식이 잘못되었습니다.";
                log.error(
                        "[ERROR] type=InvalidRequestBody, requestId={}, message={}, stackTrace={}",
                        requestId, errorMessage, ExceptionUtils.getStackTrace(e));
            }
        }

        List<ErrorResponse.FieldError> fieldErrors = List.of(
                new ErrorResponse.FieldError(
                        fieldName,
                        "",
                        errorMessage
                )
        );

        ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, fieldErrors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 8. 내부 파라미터가 잘못됨
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException e) {
        String requestId = MDC.get("requestId");
        log.error("[ERROR] type=IllegalArgument, requestId={}, message={}, stackTrace={}",
                requestId, e.getMessage(), ExceptionUtils.getStackTrace(e));

        List<ErrorResponse.FieldError> fieldErrors = List.of(
                new ErrorResponse.FieldError(
                        e.getMessage(),
                        null,
                        null
                )
        );

        ErrorResponse response = ErrorResponse.of(ErrorCode.ILLEGAL_ARGUMENT, fieldErrors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // AsyncException 처리
    @ExceptionHandler(AsyncException.class)
    protected void handleAsyncException(AsyncException e) {
        String requestId = MDC.get("requestId");
        log.error("[ERROR] type=AsyncException, requestId={}, message={}",
                requestId, e.getMessage());
        // AsyncExceptionHandler가 처리하도록 다시 throw
        throw e;
    }

    // 9. 나머지 모든 예외
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        String requestId = MDC.get("requestId");
        log.error(
                "[ERROR] type=UnhandledException, requestId={}, exceptionClass={}, message={}, stackTrace={}",
                requestId, e.getClass().getSimpleName(), e.getMessage(),
                ExceptionUtils.getStackTrace(e));

        List<ErrorResponse.FieldError> fieldErrors = List.of(
                new ErrorResponse.FieldError(
                        e.getMessage(),
                        null,
                        null
                )
        );

        ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, fieldErrors);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
