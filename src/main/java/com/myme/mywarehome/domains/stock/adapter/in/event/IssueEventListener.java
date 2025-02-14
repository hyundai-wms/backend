package com.myme.mywarehome.domains.stock.adapter.in.event;

import com.myme.mywarehome.domains.issue.application.port.in.event.IssueStockAssignEvent;
import com.myme.mywarehome.domains.stock.application.domain.Stock;
import com.myme.mywarehome.domains.stock.application.port.in.IssueAssignWithStockUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IssueEventListener {
    private final IssueAssignWithStockUseCase issueAssignWithStockUseCase;

    @EventListener
    public void handleIssueStockAssignEvent(IssueStockAssignEvent event) {
        Stock assignedStock = issueAssignWithStockUseCase.assignIssue(
                event.issue(),
                event.stockId()
        );

        // future 있으면 결과 반환
        if (event.future() != null) {
            event.future().complete(assignedStock);
        }
    }
}
