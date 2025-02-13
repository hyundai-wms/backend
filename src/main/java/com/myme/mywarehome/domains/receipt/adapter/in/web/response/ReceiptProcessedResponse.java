package com.myme.mywarehome.domains.receipt.adapter.in.web.response;

import com.myme.mywarehome.domains.stock.application.domain.Stock;
import com.myme.mywarehome.infrastructure.util.helper.DateFormatHelper;
import java.time.LocalDateTime;

public record ReceiptProcessedResponse(
        Long itemId,
        String productNumber,
        String productName,
        Integer eachCount,
        String bayNumber,
        Long receiptPlanId,
        String receiptPlanCode,
        String receiptPlanDate,
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
    public static ReceiptProcessedResponse from(Stock stock) {
        return new ReceiptProcessedResponse(
                stock.getStockId(),
                stock.getReceipt().getReceiptPlan().getProduct().getProductNumber(),
                stock.getReceipt().getReceiptPlan().getProduct().getProductName(),
                stock.getReceipt().getReceiptPlan().getProduct().getEachCount(),
                stock.getBin().getBay().getBayNumber(),
                stock.getReceipt().getReceiptPlan().getReceiptPlanId(),
                stock.getReceipt().getReceiptPlan().getReceiptPlanCode(),
                DateFormatHelper.formatDate(stock.getReceipt().getReceiptPlan().getReceiptPlanDate()),
                stock.getReceipt().getReceiptId(),
                stock.getReceipt().getReceiptCode(),
                DateFormatHelper.formatDate(stock.getReceipt().getReceiptDate()),
                null,
                null,
                null,
                stock.getReceipt().getReceiptPlan().getProduct().getCompany().getCompanyId(),
                stock.getReceipt().getReceiptPlan().getProduct().getCompany().getCompanyCode(),
                stock.getReceipt().getReceiptPlan().getProduct().getCompany().getCompanyName(),
                stock.getCreatedAt(),
                stock.getUpdatedAt()
        );
    }
}
