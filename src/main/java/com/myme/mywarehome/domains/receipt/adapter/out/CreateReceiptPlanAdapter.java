package com.myme.mywarehome.domains.receipt.adapter.out;

import com.myme.mywarehome.domains.receipt.adapter.out.persistence.ReceiptPlanJpaRepository;
import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.port.out.CreateReceiptPlanPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateReceiptPlanAdapter implements CreateReceiptPlanPort {
    private final ReceiptPlanJpaRepository receiptPlanJpaRepository;

    @Override
    public ReceiptPlan create(ReceiptPlan receiptPlan) {
        return receiptPlanJpaRepository.save(receiptPlan);
    }

    @Override
    public List<ReceiptPlan> createBulk(List<ReceiptPlan> receiptPlanList) {
        return receiptPlanJpaRepository.saveAll(receiptPlanList);
    }
}
