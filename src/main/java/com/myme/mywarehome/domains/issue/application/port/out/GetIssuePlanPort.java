package com.myme.mywarehome.domains.issue.application.port.out;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.in.command.GetAllIssuePlanCommand;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.myme.mywarehome.domains.issue.application.port.in.result.TodayIssueResult;
import com.myme.mywarehome.domains.receipt.application.port.in.result.TodayReceiptResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface GetIssuePlanPort {
    Optional<IssuePlan> getIssuePlanById(Long issuePlanId);
    Page<IssuePlan> findAllIssuePlans (GetAllIssuePlanCommand command, Pageable pageable);
    boolean existsIssuePlanById(Long issuePlanId);
    List<IssuePlan> findAllIssuePlansByDate(LocalDate selectedDate);
    Page<TodayIssueResult> findTodayIssues(LocalDate today, Pageable pageable);
    Optional<TodayIssueResult> findTodayIssueById(Long issueId, LocalDate selectedDate);
    Flux<ServerSentEvent<Object>> subscribeTodayIssues(LocalDate selectedDate, int page, int size);
    void emitTodayIssueUpdate(TodayIssueResult updatedResult, LocalDate selectedDate);
}
