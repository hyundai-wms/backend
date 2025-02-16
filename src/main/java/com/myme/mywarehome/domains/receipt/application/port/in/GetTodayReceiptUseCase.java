package com.myme.mywarehome.domains.receipt.application.port.in;

import com.myme.mywarehome.domains.receipt.application.port.in.result.TodayReceiptResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface GetTodayReceiptUseCase {
    Page<TodayReceiptResult> getTodayReceipt(LocalDate selectedDate, Pageable pageable);
}
