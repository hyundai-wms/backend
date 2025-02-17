package com.myme.mywarehome.domains.receipt.adapter.in.event;

import com.myme.mywarehome.domains.receipt.application.port.in.GetTodayReceiptUseCase;
import com.myme.mywarehome.domains.receipt.application.port.in.event.ReceiptPlanBulkStatusChangedEvent;
import com.myme.mywarehome.domains.receipt.application.port.in.event.ReceiptPlanStatusChangedEvent;
import com.myme.mywarehome.domains.receipt.application.port.in.result.TodayReceiptResult;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReceiptPlanStatusEventListener {
    private final GetTodayReceiptUseCase getTodayReceiptUseCase;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReceiptPlanStatusChanged(ReceiptPlanStatusChangedEvent event) {

        // 1. 변경된 아이템만 조회
        TodayReceiptResult updatedItem = getTodayReceiptUseCase.getTodayReceiptById(
                event.receiptPlanId(),
                event.selectedDate()
        );

        // 2. 모든 구독자에게 아이템 정보만 전송
        getTodayReceiptUseCase.notifyReceiptUpdate(updatedItem, event.selectedDate());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReceiptPlanBulkStatusChanged(ReceiptPlanBulkStatusChangedEvent event) {
        // 변경된 모든 plan들의 최신 데이터를 한 번에 조회
        event.receiptPlanIds().forEach(planId -> {
            TodayReceiptResult updatedResult = getTodayReceiptUseCase.getTodayReceiptById(
                    planId,
                    event.selectedDate()
            );
            getTodayReceiptUseCase.notifyReceiptUpdate(updatedResult, event.selectedDate());
        });
    }

}
