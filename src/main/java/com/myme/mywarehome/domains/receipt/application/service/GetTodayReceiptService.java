package com.myme.mywarehome.domains.receipt.application.service;

import com.myme.mywarehome.domains.receipt.application.port.in.GetTodayReceiptUseCase;
import com.myme.mywarehome.domains.receipt.application.port.in.result.TodayReceiptResult;
import com.myme.mywarehome.domains.receipt.application.port.out.GetReceiptPlanPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GetTodayReceiptService implements GetTodayReceiptUseCase {
    private final GetReceiptPlanPort getReceiptPlanPort;

    @Override
    public Page<TodayReceiptResult> getTodayReceipt(LocalDate selectedDate, Pageable pageable) {
        return getReceiptPlanPort.findTodayReceipts(selectedDate, pageable);
    }
}
