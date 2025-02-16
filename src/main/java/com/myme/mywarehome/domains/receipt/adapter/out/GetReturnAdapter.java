package com.myme.mywarehome.domains.receipt.adapter.out;

import com.myme.mywarehome.domains.receipt.adapter.out.persistence.ReturnJpaRepository;
import com.myme.mywarehome.domains.receipt.application.port.out.GetReturnPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetReturnAdapter implements GetReturnPort {
    private final ReturnJpaRepository returnJpaRepository;

    @Override
    public long countByReceiptPlanId(Long receiptPlanId) {
        return returnJpaRepository.countByReceiptPlanId(receiptPlanId);
    }
}
