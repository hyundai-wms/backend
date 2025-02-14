package com.myme.mywarehome.domains.receipt.adapter.out;

import com.myme.mywarehome.domains.receipt.adapter.out.persistence.ReceiptPlanJpaRepository;
import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.port.in.command.GetAllReceiptPlanCommand;
import com.myme.mywarehome.domains.receipt.application.port.in.result.TodayReceiptResult;
import com.myme.mywarehome.domains.receipt.application.port.out.GetReceiptPlanPort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetReceiptPlanAdapter implements GetReceiptPlanPort {
    private final ReceiptPlanJpaRepository receiptPlanJpaRepository;

    @Override
    public Optional<ReceiptPlan> findReceiptPlanById(Long receiptPlanId) {
        return receiptPlanJpaRepository.findById(receiptPlanId);
    }

    @Override
    public Page<ReceiptPlan> findAllReceiptPlans(GetAllReceiptPlanCommand command,
            Pageable pageable) {
        return receiptPlanJpaRepository.findByConditions(
                command.companyName(),
                command.productName(),
                command.companyCode(),
                command.receiptPlanStartDate(),
                command.receiptPlanEndDate(),
                command.productNumber(),
                pageable);
    }

    @Override
    public boolean existsReceiptPlanById(Long receiptPlanId) {
        return receiptPlanJpaRepository.existsById(receiptPlanId);
    }

    @Override
    public List<ReceiptPlan> findAllReceiptPlansByDate(LocalDate selectedDate) {
        return receiptPlanJpaRepository.findByReceiptPlanDate(selectedDate);
    }

    @Override
    public Page<TodayReceiptResult> findTodayReceipts(LocalDate today, Pageable pageable) {
        return receiptPlanJpaRepository.findTodayReceipts(
                today,
                pageable
        );
    }
}
