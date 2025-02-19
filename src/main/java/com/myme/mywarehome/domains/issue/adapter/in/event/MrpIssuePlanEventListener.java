package com.myme.mywarehome.domains.issue.adapter.in.event;

import com.myme.mywarehome.domains.issue.application.port.in.CreateIssuePlanUseCase;
import com.myme.mywarehome.domains.mrp.application.port.in.event.CreatePlanFromMrpEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MrpIssuePlanEventListener {
    private final CreateIssuePlanUseCase createIssuePlanUseCase;

    @Async
    @TransactionalEventListener
    public void handleCreatePlanFromMrp(CreatePlanFromMrpEvent event) {
        // 출고 계획 생성
        createIssuePlanUseCase.createBulk(event.issuePlanCommands());
    }
}
