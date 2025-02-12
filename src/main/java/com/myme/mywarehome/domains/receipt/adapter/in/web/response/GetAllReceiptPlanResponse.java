package com.myme.mywarehome.domains.receipt.adapter.in.web.response;

import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import java.util.List;
import org.springframework.data.domain.Page;

public record GetAllReceiptPlanResponse(
        List<ReceiptPlanResponse> content,
        Integer pageNumber,
        Integer pageSize,
        Long totalElements,
        Integer totalPages,
        Boolean isFirst,
        Boolean isLast
) {
    public static GetAllReceiptPlanResponse from(Page<ReceiptPlan> receiptPlanList) {
        return new GetAllReceiptPlanResponse(
                receiptPlanList.getContent().stream()
                        .map(ReceiptPlanResponse::from)
                        .toList(),
                receiptPlanList.getNumber(),
                receiptPlanList.getSize(),
                receiptPlanList.getTotalElements(),
                receiptPlanList.getTotalPages(),
                receiptPlanList.isFirst(),
                receiptPlanList.isLast()
        );
    }
}
