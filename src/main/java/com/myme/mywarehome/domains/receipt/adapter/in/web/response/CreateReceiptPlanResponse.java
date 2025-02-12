package com.myme.mywarehome.domains.receipt.adapter.in.web.response;

import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import java.time.LocalDateTime;

public record CreateReceiptPlanResponse(
        Long receiptPlanId,
        String receiptPlanCode,
        String productNumber,
        String productName,
        Integer itemCount,
        Long companyId,
        String companyCode,
        String receiptPlanDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CreateReceiptPlanResponse from(ReceiptPlan receiptPlan) {
        return new CreateReceiptPlanResponse(
                receiptPlan.getReceiptPlanId(),
                receiptPlan.getReceiptPlanCode(),
                receiptPlan.getProduct().getProductNumber(),
                receiptPlan.getProduct().getProductName(),
                receiptPlan.getReceiptPlanItemCount(),
                receiptPlan.getProduct().getCompany().getCompanyId(),
                receiptPlan.getProduct().getCompany().getCompanyCode(),
                receiptPlan.getReceiptPlanDate().toString(),
                receiptPlan.getCreatedAt(),
                receiptPlan.getUpdatedAt()
        );
    }
}
