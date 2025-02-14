package com.myme.mywarehome.infrastructure.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.myme.mywarehome.infrastructure.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
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
        log.error("handleBusinessException", e);
        ErrorCode errorCode = e.getErrorCode();
        List<ErrorResponse.FieldError> fieldErrors = e.getReason();

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
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        List<ErrorResponse.FieldError> fieldErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorResponse.FieldError(
                        error.getField(),
                        error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                        error.getDefaultMessage()))
                .toList();

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, fieldErrors));
    }

    // 3. 경로 변수 타입 불일치
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.error("handleMethodArgumentTypeMismatch", e);
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
    protected ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("handleMissingServletRequestParameterException", e);

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
    protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.error("handleHttpRequestMethodNotSupported", e);
        ErrorResponse response = ErrorResponse.of(ErrorCode.METHOD_NOT_ALLOWED);
        return new ResponseEntity<>(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // 6. 컨트롤러를 찾을 수 없음
    @ExceptionHandler(NoResourceFoundException.class)
    protected ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        log.error("handleNoResourceFoundException", e);
        ErrorResponse response = ErrorResponse.of(ErrorCode.ROUTE_NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // 7. Request Body 누락 또는 형식 오류
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.error("handleHttpMessageNotReadable", e);

        String errorMessage;
        String fieldName = "requestBody";

        if (e.getMessage().contains("Required request body is missing")) {
            errorMessage = "요청 본문이 필요합니다.";
        } else {
            // JsonParseException, JsonMappingException 등의 상세 에러 정보 추출
            Throwable cause = e.getCause();
            if (cause instanceof JsonParseException jpe) {
                fieldName = "JSON parsing error";
                errorMessage = String.format("위치 %s에서 JSON 파싱 오류가 발생했습니다: %s",
                        jpe.getLocation(), jpe.getOriginalMessage());
            } else if (cause instanceof JsonMappingException jme) {
                fieldName = jme.getPath().isEmpty() ? "Unknown" :
                        jme.getPath().get(0).getFieldName();
                errorMessage = String.format("필드 '%s'의 값이 잘못되었습니다: %s",
                        fieldName, jme.getOriginalMessage());
            } else {
                errorMessage = "요청 본문의 형식이 잘못되었습니다.";
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
    protected ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("handleIllegalArgumentException", e);

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

    @ExceptionHandler(AsyncException.class)
    protected void handleAsyncException(AsyncException e) {
        // AsyncExceptionHandler가 처리
        throw e;
    }

    // 9. 나머지 모든 예외
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {

        List<ErrorResponse.FieldError> fieldErrors = List.of(
                new ErrorResponse.FieldError(
                        e.getMessage(),
                        null,
                        null
                )
        );

        log.error("handleException", e);
        ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, fieldErrors);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
