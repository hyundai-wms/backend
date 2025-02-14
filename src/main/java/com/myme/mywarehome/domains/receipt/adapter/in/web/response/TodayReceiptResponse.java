package com.myme.mywarehome.domains.receipt.adapter.in.web.response;

import com.myme.mywarehome.domains.receipt.application.port.in.result.TodayReceiptResult;
import org.springframework.data.domain.Page;

import java.util.List;

public record TodayReceiptResponse (
    List<TodayReceiptResult> content,
    Integer pageNumber,
    Integer pageSize,
    Long totalElements,
    Integer totalPages,
    Boolean isFirst,
    Boolean isLast
) {
    public static TodayReceiptResponse from(Page<TodayReceiptResult> content) {
        return new TodayReceiptResponse(
                content.getContent(),
                content.getNumber(),
                content.getSize(),
                content.getTotalElements(),
                content.getTotalPages(),
                content.isFirst(),
                content.isLast()
        );
    }
}
