package com.myme.mywarehome.domains.receipt.application.service;

import com.myme.mywarehome.domains.receipt.application.port.in.GetTodayReceiptUseCase;
import com.myme.mywarehome.domains.receipt.application.port.in.command.SelectedDateCommand;
import com.myme.mywarehome.domains.receipt.application.port.in.result.TodayReceiptResult;
import com.myme.mywarehome.domains.receipt.application.port.out.GetReceiptPlanPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetTodayReceiptService implements GetTodayReceiptUseCase {
    private final GetReceiptPlanPort getReceiptPlanPort;

    @Override
    public Page<TodayReceiptResult> getTodayReceipt(SelectedDateCommand command, Pageable pageable) {
        return getReceiptPlanPort.findTodayReceipts(command.selectedDate(), pageable);
    }
}
