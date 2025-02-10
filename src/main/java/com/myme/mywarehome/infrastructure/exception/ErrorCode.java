package com.myme.mywarehome.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE(400, "C001", "요청 파라미터가 유효하지 않습니다."),
    INVALID_TYPE_VALUE(400, "C002", "요청 파라미터 타입이 유효하지 않습니다."),
    MISSING_INPUT_VALUE(400, "C003", "필수 파라미터가 누락되었습니다."),
    METHOD_NOT_ALLOWED(405, "C004", "HTTP 메소드가 유효하지 않습니다."),
    ROUTE_NOT_FOUND(404, "C005", "요청 경로를 찾을 수 없습니다."),
    ILLEGAL_ARGUMENT(400, "C006", "내부 파라미터가 유효하지 않습니다."),
    INTERNAL_SERVER_ERROR(500, "C100", "서버 에러.");

    private final int status;
    private final String code;
    private final String message;
}
