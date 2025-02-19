package com.myme.mywarehome.domains.issue.application.port.in;

import com.myme.mywarehome.domains.issue.application.port.in.result.TodayIssueResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface GetTodayIssueUseCase {
    Page<TodayIssueResult> getTodayIssue(LocalDate selectedDate, Pageable pageable);
    TodayIssueResult getTodayIssueById(Long issueId, LocalDate selectedDate);
    Flux<ServerSentEvent<Object>> subscribeTodayIssues(LocalDate selectedDate, int page, int size);
    void notifyIssueUpdate(TodayIssueResult updatedResult, LocalDate selectedDate);
}
