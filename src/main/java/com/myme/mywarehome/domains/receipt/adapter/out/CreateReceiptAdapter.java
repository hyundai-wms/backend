package com.myme.mywarehome.domains.receipt.adapter.out;

import com.myme.mywarehome.domains.receipt.adapter.out.persistence.ReceiptJpaRepository;
import com.myme.mywarehome.domains.receipt.application.domain.Receipt;
import com.myme.mywarehome.domains.receipt.application.port.out.CreateReceiptPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateReceiptAdapter implements CreateReceiptPort {
    private final ReceiptJpaRepository receiptJpaRepository;

    @Override
    public Receipt create(Receipt receipt) {
        return receiptJpaRepository.save(receipt);
    }
}
