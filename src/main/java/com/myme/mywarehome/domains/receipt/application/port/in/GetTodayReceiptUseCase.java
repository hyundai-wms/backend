package com.myme.mywarehome.domains.receipt.application.port.in;

import com.myme.mywarehome.domains.receipt.application.port.in.result.TodayReceiptResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface GetTodayReceiptUseCase {
    Page<TodayReceiptResult> getTodayReceipt(LocalDate selectedDate, Pageable pageable);
    TodayReceiptResult getTodayReceiptById(Long receiptId, LocalDate selectedDate);
    Flux<ServerSentEvent<Object>> subscribeTodayReceipts(LocalDate selectedDate, int page, int size);
    void notifyReceiptUpdate(TodayReceiptResult updatedResult, LocalDate selectedDate);
}
