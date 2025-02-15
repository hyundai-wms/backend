package com.myme.mywarehome.domains.receipt.adapter.out;

import com.myme.mywarehome.domains.receipt.adapter.out.persistence.OutboundProductJpaRepository;
import com.myme.mywarehome.domains.receipt.application.port.out.GetOutboundProductPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetOutboundProductAdapter implements GetOutboundProductPort {
    private final OutboundProductJpaRepository outboundProductJpaRepository;

    @Override
    public boolean existsByOutboundProductId(String outboundProductId) {
        return outboundProductJpaRepository.existsByOutboundProductId(outboundProductId);
    }

    @Override
    public long countByReceiptPlanId(Long receiptPlanId) {
        return outboundProductJpaRepository.countByReceiptPlanId(receiptPlanId);
    }

    @Override
    public List<String> findOutboundProductIdsByReceiptPlanId(Long receiptPlanId) {
        return outboundProductJpaRepository.findOutboundProductIdsByReceiptPlanId(receiptPlanId);
    }
}
