package com.myme.mywarehome.domains.issue.application.service;

import com.myme.mywarehome.domains.issue.adapter.out.exception.IssuePlanItemCountExceededException;
import com.myme.mywarehome.domains.issue.adapter.out.exception.IssuePlanNotFoundException;
import com.myme.mywarehome.domains.issue.adapter.out.exception.StockAlreadyIssuedException;
import com.myme.mywarehome.domains.issue.adapter.out.exception.StockAssignTimeoutException;
import com.myme.mywarehome.domains.issue.application.domain.Issue;
import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.in.IssueProcessUseCase;
import com.myme.mywarehome.domains.issue.application.port.in.command.IssueProcessCommand;
import com.myme.mywarehome.domains.issue.application.port.in.event.IssueStockAssignEvent;
import com.myme.mywarehome.domains.issue.application.port.out.CreateIssuePort;
import com.myme.mywarehome.domains.issue.application.port.out.GetIssuePlanPort;
import com.myme.mywarehome.domains.issue.application.port.out.GetIssuePort;
import com.myme.mywarehome.domains.stock.application.domain.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class IssueProcessService implements IssueProcessUseCase {
    private final GetIssuePort getIssuePort;
    private final GetIssuePlanPort getIssuePlanPort;
    private final CreateIssuePort createIssuePort;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Issue process(Long stockId, IssueProcessCommand command, LocalDate selectedDate) {
        // 1. 해당 물품이 이미 출고 되었는지 확인
        // 주어진 stockId에 해당하는 Issue 엔티티가 존재하면 true, 없으면 false를 반환
        if (getIssuePort.existsByStockId(stockId)) {
            throw new StockAlreadyIssuedException();
        }

        // 2. 출고 예정 정보 조회
        IssuePlan issuePlan = getIssuePlanPort.getIssuePlanById(command.issuePlanId())
                .orElseThrow(IssuePlanNotFoundException::new);

        // 3. 출고 수량 체크
        // processedCount = issuePlanId를 갖는 Issue의 개수
        long processedCount = getIssuePort.countProcessedIssueByIssuePlanId(issuePlan.getIssuePlanId());
        if (processedCount >= issuePlan.getIssuePlanItemCount()) {
            throw new IssuePlanItemCountExceededException();
        }

        // 4. 출고 기록 create
        Issue issue = Issue.builder()
                .issuePlan(issuePlan)
                .product(issuePlan.getProduct())
                .issueDate(selectedDate)
                .build();

        Issue createdIssue = createIssuePort.create(issue);

        // 5. Stock 연결을 위한 이벤트 발생
        // Stock을 받아오기 위한 Future 사용
        CompletableFuture<Stock> future = new CompletableFuture<>();
        eventPublisher.publishEvent(new IssueStockAssignEvent(createdIssue, stockId, future));

        try {
            Stock stock = future.get(5, TimeUnit.SECONDS);
            // Todo: stock으로 추가 검증이 필요하다면 여기서 수행
        } catch (Exception e) {
            throw new StockAssignTimeoutException();
        }

        return createdIssue;

    }
}



