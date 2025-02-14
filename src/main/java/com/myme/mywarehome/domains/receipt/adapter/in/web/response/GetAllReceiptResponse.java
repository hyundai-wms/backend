package com.myme.mywarehome.domains.receipt.adapter.in.web.response;

import com.myme.mywarehome.domains.receipt.application.domain.Receipt;
import org.springframework.data.domain.Page;

import java.util.List;

public record GetAllReceiptResponse(
        List<ReceiptResponse> content,
        Integer pageNumber,
        Integer pageSize,
        Long totalElements,
        Integer totalPages,
        Boolean isFirst,
        Boolean isLast
) {
    public static GetAllReceiptResponse from(Page<Receipt> receiptList) {
        return new GetAllReceiptResponse(
                receiptList.getContent().stream()
                        .map(ReceiptResponse::from)
                        .toList(),
                receiptList.getNumber(),
                receiptList.getSize(),
                receiptList.getTotalElements(),
                receiptList.getTotalPages(),
                receiptList.isFirst(),
                receiptList.isLast()
        );
    }
}
