package com.myme.mywarehome.domains.receipt.adapter.in.web.response;

import com.myme.mywarehome.domains.receipt.application.domain.Receipt;
import com.myme.mywarehome.infrastructure.util.helper.DateFormatHelper;

import java.time.LocalDateTime;

public record ReceiptResponse(
        Long receiptId,
        String receiptCode,
        String receiptDate,
        Long receiptPlanId,
        String receiptPlanCode,
        String receiptPlanDate,
        String productNumber,
        String productName,
        Long companyId,
        String companyCode,
        String companyName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReceiptResponse from(Receipt receipt) {
        return new ReceiptResponse(
                receipt.getReceiptId(),
                receipt.getReceiptCode(),
                DateFormatHelper.formatDate(receipt.getReceiptDate()),
                receipt.getReceiptPlan().getReceiptPlanId(),
                receipt.getReceiptPlan().getReceiptPlanCode(),
                DateFormatHelper.formatDate(receipt.getReceiptPlan().getReceiptPlanDate()),
                receipt.getReceiptPlan().getProduct().getProductNumber(),
                receipt.getReceiptPlan().getProduct().getProductName(),
                receipt.getReceiptPlan().getProduct().getCompany().getCompanyId(),
                receipt.getReceiptPlan().getProduct().getCompany().getCompanyCode(),
                receipt.getReceiptPlan().getProduct().getCompany().getCompanyName(),
                receipt.getCreatedAt(),
                receipt.getUpdatedAt()
        );
    }
}
