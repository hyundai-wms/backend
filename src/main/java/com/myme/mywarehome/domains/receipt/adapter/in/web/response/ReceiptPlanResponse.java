package com.myme.mywarehome.domains.receipt.adapter.in.web.response;

import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.infrastructure.util.helper.DateFormatHelper;
import java.time.LocalDateTime;

public record ReceiptPlanResponse(
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
    public static ReceiptPlanResponse from(ReceiptPlan receiptPlan) {
        return new ReceiptPlanResponse(
                receiptPlan.getReceiptPlanId(),
                receiptPlan.getReceiptPlanCode(),
                receiptPlan.getProduct().getProductNumber(),
                receiptPlan.getProduct().getProductName(),
                receiptPlan.getReceiptPlanItemCount(),
                receiptPlan.getProduct().getCompany().getCompanyId(),
                receiptPlan.getProduct().getCompany().getCompanyCode(),
                DateFormatHelper.formatDate(receiptPlan.getReceiptPlanDate()),
                receiptPlan.getCreatedAt(),
                receiptPlan.getUpdatedAt()
        );
    }
}
