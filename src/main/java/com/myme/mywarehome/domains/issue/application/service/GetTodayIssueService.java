package com.myme.mywarehome.domains.issue.application.service;

import com.myme.mywarehome.domains.issue.adapter.out.exception.IssuePlanNotFoundException;
import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.in.GetTodayIssueUseCase;
import com.myme.mywarehome.domains.issue.application.port.in.result.TodayIssueResult;
import com.myme.mywarehome.domains.issue.application.port.out.GetIssuePlanPort;
import com.myme.mywarehome.domains.receipt.application.exception.ReceiptNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GetTodayIssueService implements GetTodayIssueUseCase {
    private final GetIssuePlanPort getIssuePlanPort;


    @Override
    public Page<TodayIssueResult> getTodayIssue(LocalDate selectedDate, Pageable pageable) {
        return getIssuePlanPort.findTodayIssues(selectedDate, pageable);
    }

    @Override
    public TodayIssueResult getTodayIssueById(Long issueId, LocalDate selectedDate) {
        return getIssuePlanPort.findTodayIssueById(issueId, selectedDate).orElseThrow(IssuePlanNotFoundException::new);
    }

    @Override
    public Flux<ServerSentEvent<Object>> subscribeTodayIssues(LocalDate selectedDate, int page, int size) {
        return getIssuePlanPort.subscribeTodayIssues(selectedDate, page, size);
    }

    @Override
    public void notifyIssueUpdate(TodayIssueResult updatedResult, LocalDate selectedDate) {
        getIssuePlanPort.emitTodayIssueUpdate(updatedResult, selectedDate);
    }
}
