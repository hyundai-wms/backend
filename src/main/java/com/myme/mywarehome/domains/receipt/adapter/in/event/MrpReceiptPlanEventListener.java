package com.myme.mywarehome.domains.receipt.adapter.in.event;

import com.myme.mywarehome.domains.mrp.application.port.in.event.CreatePlanFromMrpEvent;
import com.myme.mywarehome.domains.receipt.application.port.in.CreateReceiptPlanUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MrpReceiptPlanEventListener {
    private final CreateReceiptPlanUseCase createReceiptPlanUseCase;

    @Async
    @TransactionalEventListener
    public void handleCreatePlanFromMrp(CreatePlanFromMrpEvent event) {
        // 입고 계획 생성
        createReceiptPlanUseCase.createReceiptPlanBulk(event.receiptPlanCommands());
    }
}
