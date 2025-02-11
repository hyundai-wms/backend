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
    INTERNAL_SERVER_ERROR(500, "C100", "서버 에러."),

    // User
    USER_NOT_FOUND(404, "U001", "해당 유저가 존재하지 않습니다."),
    USER_DUPLICATED(409, "U002", "이미 등록된 유저입니다."),
    USER_ROLE_UPDATE_FAILED(409, "U003", "총관리자의 본인의 역할을 수정할 수 없습니다."),
    USER_DELETION_FAILED(409, "U004", "총관리자 본인 스스로 삭제할 수 없습니다."),

    // Auth
    LOGIN_FAILED(401, "A001", "아이디 또는 비밀번호가 일치하지 않습니다."),
    INVALID_CREDENTIALS(401, "A002", "인증 정보가 유효하지 않습니다."),
    SESSION_EXPIRED(401, "A003", "세션이 만료되었습니다."),
    UNAUTHORIZED(401, "A004", "인증되지 않은 사용자입니다."),
    FORBIDDEN(403, "A005", "권한이 없습니다."),
    INVALID_CURRENT_PASSWORD(400, "A006", "기존 비밀번호가 유효하지 않습니다."),
    ;

    private final int status;
    private final String code;
    private final String message;
}
