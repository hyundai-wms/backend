package com.myme.mywarehome.domains.receipt.application.service;

import com.myme.mywarehome.domains.receipt.application.exception.ReceiptNotFoundException;
import com.myme.mywarehome.domains.receipt.application.port.in.GetTodayReceiptUseCase;
import com.myme.mywarehome.domains.receipt.application.port.in.result.TodayReceiptResult;
import com.myme.mywarehome.domains.receipt.application.port.out.GetReceiptPlanPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GetTodayReceiptService implements GetTodayReceiptUseCase {
    private final GetReceiptPlanPort getReceiptPlanPort;

    @Override
    public Page<TodayReceiptResult> getTodayReceipt(LocalDate selectedDate, Pageable pageable) {
        return getReceiptPlanPort.findTodayReceipts(selectedDate, pageable);
    }

    @Override
    public TodayReceiptResult getTodayReceiptById(Long receiptId, LocalDate selectedDate) {
        return getReceiptPlanPort.findTodayReceiptById(receiptId, selectedDate)
                .orElseThrow(ReceiptNotFoundException::new);
    }

    @Override
    public Flux<ServerSentEvent<Object>> subscribeTodayReceipts(LocalDate selectedDate, int page, int size) {
        return getReceiptPlanPort.subscribeTodayReceipts(selectedDate, page, size);
    }

    @Override
    public void notifyReceiptUpdate(TodayReceiptResult updatedResult, LocalDate selectedDate) {
        getReceiptPlanPort.emitTodayReceiptUpdate(updatedResult, selectedDate);
    }
}
