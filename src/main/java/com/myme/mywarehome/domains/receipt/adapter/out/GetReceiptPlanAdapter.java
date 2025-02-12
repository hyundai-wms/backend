package com.myme.mywarehome.domains.receipt.adapter.out;

import com.myme.mywarehome.domains.receipt.adapter.out.persistence.ReceiptPlanJpaRepository;
import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.port.out.GetReceiptPlanPort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetReceiptPlanAdapter implements GetReceiptPlanPort {
    private final ReceiptPlanJpaRepository receiptPlanJpaRepository;

    @Override
    public Optional<ReceiptPlan> getReceiptPlanById(Long receiptPlanId) {
        return receiptPlanJpaRepository.findById(receiptPlanId);
    }

    @Override
    public boolean existsReceiptPlanById(Long receiptPlanId) {
        return receiptPlanJpaRepository.existsById(receiptPlanId);
    }
}
