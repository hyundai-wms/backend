package com.myme.mywarehome.domains.receipt.application.port.out;

import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.port.in.command.GetAllReceiptPlanCommand;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.myme.mywarehome.domains.receipt.application.port.in.result.TodayReceiptResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetReceiptPlanPort {
    Optional<ReceiptPlan> findReceiptPlanById(Long receiptPlanId);
    Page<ReceiptPlan> findAllReceiptPlans(GetAllReceiptPlanCommand command, Pageable pageable, LocalDate selectedDate);
    boolean existsReceiptPlanById(Long receiptPlanId);
    List<ReceiptPlan> findAllReceiptPlansByDate(LocalDate selectedDate);
    Page<TodayReceiptResult> findTodayReceipts(LocalDate today, Pageable pageable);
}
