package com.myme.mywarehome.domains.receipt.adapter.out;

import com.myme.mywarehome.domains.receipt.adapter.out.persistence.ReceiptPlanJpaRepository;
import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.port.out.UpdateReceiptPlanPort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateReceiptPlanAdapter implements UpdateReceiptPlanPort {
    private final ReceiptPlanJpaRepository receiptPlanJpaRepository;

    @Override
    public Optional<ReceiptPlan> updateReceiptPlan(ReceiptPlan receiptPlan) {
        if(receiptPlan.getReceiptPlanId() != null) {
            return Optional.of(receiptPlanJpaRepository.save(receiptPlan));
        } else {
            log.error("ReceiptPlan Not Changed : ReceiptPlan Id is null");
            return Optional.empty();
        }
    }
}
