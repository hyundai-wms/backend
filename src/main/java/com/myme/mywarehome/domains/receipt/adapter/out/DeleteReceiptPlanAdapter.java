package com.myme.mywarehome.domains.receipt.adapter.out;

import com.myme.mywarehome.domains.receipt.adapter.out.persistence.ReceiptPlanJpaRepository;
import com.myme.mywarehome.domains.receipt.application.port.out.DeleteReceiptPlanPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteReceiptPlanAdapter implements DeleteReceiptPlanPort {
    private final ReceiptPlanJpaRepository receiptPlanJpaRepository;

    @Override
    public void delete(Long receiptPlanId) {
        receiptPlanJpaRepository.deleteById(receiptPlanId);
    }
}
