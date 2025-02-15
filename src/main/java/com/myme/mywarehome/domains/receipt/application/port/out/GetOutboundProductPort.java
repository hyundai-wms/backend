package com.myme.mywarehome.domains.receipt.application.port.out;

import com.myme.mywarehome.domains.receipt.application.domain.OutboundProduct;

import java.util.Map;

public interface GetOutboundProductPort {
    boolean existsByOutboundProductId(String outboundProductId);
    long countByReceiptPlanId(Long receiptPlanId);
    Map<String, Long> getProcessedStatusByReceiptPlanId(Long receiptPlanId);
}
