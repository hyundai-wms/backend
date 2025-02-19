package com.myme.mywarehome.domains.issue.adapter.in.event;

import com.myme.mywarehome.domains.issue.application.port.in.GetTodayIssueUseCase;
import com.myme.mywarehome.domains.issue.application.port.in.event.IssuePlanBulkStatusChangedEvent;
import com.myme.mywarehome.domains.issue.application.port.in.event.IssuePlanStatusChangedEvent;
import com.myme.mywarehome.domains.issue.application.port.in.result.TodayIssueResult;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class IssuePlanStatusEventListener {
    private final GetTodayIssueUseCase getTodayIssueUseCase;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleIssuePlanStatusChanged(IssuePlanStatusChangedEvent event) {

        // 1. 변경된 아이템만 조회
        TodayIssueResult updatedItem = getTodayIssueUseCase.getTodayIssueById(
                event.issuePlanId(),
                event.selectedDate()
        );

        // 2. 모든 구독자에게 아이템 정보만 전송
        getTodayIssueUseCase.notifyIssueUpdate(updatedItem, event.selectedDate());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleIssuePlanBulkStatusChanged(IssuePlanBulkStatusChangedEvent event) {
        // 변경된 모든 plan들의 최신 데이터를 한 번에 조회
        event.issuePlanIds().forEach(planId -> {
            TodayIssueResult updatedResult = getTodayIssueUseCase.getTodayIssueById(
                    planId,
                    event.selectedDate()
            );
           getTodayIssueUseCase.notifyIssueUpdate(updatedResult, event.selectedDate());
        });
    }
}
