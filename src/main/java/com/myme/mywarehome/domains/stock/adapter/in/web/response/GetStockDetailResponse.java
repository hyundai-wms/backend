package com.myme.mywarehome.domains.stock.adapter.in.web.response;

import com.myme.mywarehome.domains.stock.application.domain.Stock;
import com.myme.mywarehome.infrastructure.util.helper.DateFormatHelper;
import java.time.LocalDateTime;

public record GetStockDetailResponse(
        Long itemId,
        String ItemCode,
        String productNumber,
        String productName,
        Integer eachCount,
        String bayNumber,
        Long receiptId,
        String receiptCode,
        String receiptDate,
        Long issuePlanId,
        String issuePlanCode,
        String issuePlanDate,
        Long companyId,
        String companyCode,
        String companyName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static GetStockDetailResponse from(Stock stock) {
        return new GetStockDetailResponse(
                stock.getStockId(),
                stock.getStockCode(),
                stock.getReceipt().getReceiptPlan().getProduct().getProductNumber(),
                stock.getReceipt().getReceiptPlan().getProduct().getProductName(),
                stock.getReceipt().getReceiptPlan().getProduct().getEachCount(),
                stock.getBin().getBay().getBayNumber(),
                stock.getReceipt().getReceiptId(),
                stock.getReceipt().getReceiptCode(),
                DateFormatHelper.formatDate(stock.getReceipt().getReceiptDate()),
                stock.getIssue() != null ? stock.getIssue().getIssueId() : null,
                stock.getIssue() != null ? stock.getIssue().getIssueCode() : null,
                stock.getIssue() != null ? DateFormatHelper.formatDate(stock.getIssue().getIssueDate()) : null,
                stock.getReceipt().getReceiptPlan().getProduct().getCompany().getCompanyId(),
                stock.getReceipt().getReceiptPlan().getProduct().getCompany().getCompanyCode(),
                stock.getReceipt().getReceiptPlan().getProduct().getCompany().getCompanyName(),
                stock.getCreatedAt(),
                stock.getUpdatedAt()
        );
    }
}