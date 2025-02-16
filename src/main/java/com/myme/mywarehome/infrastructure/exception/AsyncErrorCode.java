package com.myme.mywarehome.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AsyncErrorCode {
    // Stock
    STOCK_CREATION_FAILED("ASYNC_S001", "재고 생성에 실패했습니다."),
    STOCK_UPDATE_FAILED("ASYNC_S002", "재고 수정에 실패했습니다."),

    // Receipt
    RECEIPT_PROCESSING_FAILED("ASYNC_R001", "입고 처리에 실패했습니다."),

    // Order
    ORDER_PROCESSING_FAILED("ASYNC_O001", "주문 처리에 실패했습니다.");

    private final String code;
    private final String message;
}
