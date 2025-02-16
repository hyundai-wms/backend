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
    INVALID_DATE_FORMAT(400, "C007", "존재하지 않는 날짜이거나, 날짜 형식이 유효하지 않습니다."),
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

    // Product
    PRODUCT_NOT_FOUND(404, "P001", "해당 물품이 존재하지 않습니다."),

    // Receipt
    RECEIPT_PLAN_NOT_FOUND(404, "R001", "해당 입고 예정 정보가 존재하지 않습니다."),
    RECEIPT_NOT_FOUND(404, "R002", "해당 입고 정보가 존재하지 않습니다."),
    DUPLICATED_OUTBOUND_PRODUCT(409, "R003", "해당 납품된 물품은 이미 등록되었습니다."),
    OUTBOUND_PRODUCT_ID_PARSE_FAILED(400, "R004", "outboundProductId를 파싱할 수 없습니다."),
    RECEIPT_PLAN_ITEM_CAPACITY_EXCEEDED(422, "R005", "입고 처리 가능한 용량을 초과하였습니다."),
    RECEIPT_BULK_PROCESS_FAILED(500, "R006", "알 수 없는 이유로 입고 Bulk 처리 작업이 실패하였습니다."),

    // Issue
    ISSUE_PLAN_NOT_FOUND(404, "I001", "출고 예정 정보를 찾을 수 없습니다."),
    ISSUE_PLAN_EXCEED_STOCK(404, "I002", "출고 예정 수량이 현재 재고량보다 많습니다."),
    ISSUE_PLAN_ITEM_COUNT_EXCEEDED(422, "I003", "출고 예정 수량을 초과하였습니다."),

    // Stock, Bay, Bin
    NO_AVAILABLE_BIN(409, "S001", "사용가능한 BIN이 없습니다."),
    STOCK_CREATION_TIMEOUT(408, "S002", "재고를 생성 중 시간초과가 발생하였습니다."),
    STOCK_ALREADY_ISSUED(409, "I004", "이미 출고 처리된 재고입니다."),
    STOCK_ASSIGN_TIMEOUT(408, "S003", "재고 할당 처리 시간이 초과되었습니다."),
    STOCK_NOT_FOUND(404, "S004", "해당 재고를 찾을 수 없습니다."),
    ;




    private final int status;
    private final String code;
    private final String message;
}
