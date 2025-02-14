package com.myme.mywarehome.domains.receipt.application.port.in;

import com.myme.mywarehome.domains.receipt.application.port.in.command.SelectedDateCommand;
import com.myme.mywarehome.domains.receipt.application.port.in.result.TodayReceiptResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetTodayReceiptUseCase {
    Page<TodayReceiptResult> getTodayReceipt(SelectedDateCommand command, Pageable pageable);
}
