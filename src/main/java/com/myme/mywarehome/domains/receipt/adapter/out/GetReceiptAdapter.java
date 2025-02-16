package com.myme.mywarehome.domains.receipt.adapter.out;

import com.myme.mywarehome.domains.receipt.adapter.out.persistence.ReceiptJpaRepository;
import com.myme.mywarehome.domains.receipt.adapter.out.persistence.ReceiptPlanJpaRepository;
import com.myme.mywarehome.domains.receipt.application.domain.Receipt;
import com.myme.mywarehome.domains.receipt.application.port.in.command.GetAllReceiptCommand;
import com.myme.mywarehome.domains.receipt.application.port.in.result.TodayReceiptResult;
import com.myme.mywarehome.domains.receipt.application.port.out.GetReceiptPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class GetReceiptAdapter implements GetReceiptPort {
    private final ReceiptJpaRepository receiptJpaRepository;

    @Override
    public Page<Receipt> findAllReceipts(GetAllReceiptCommand command, Pageable pageable) {
        return receiptJpaRepository.findByConditions(
                command.companyCode(),
                command.companyName(),
                command.receiptPlanCode(),
                command.receiptPlanStartDate(),
                command.receiptPlanEndDate(),
                command.productNumber(),
                command.productName(),
                command.receiptCode(),
                command.receiptStartDate(),
                command.receiptEndDate(),
                pageable
        );
    }

    @Override
    public long countByReceiptPlanId(Long receiptPlanId) {
        return receiptJpaRepository.countByReceiptPlanId(receiptPlanId);
    }
}
