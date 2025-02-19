package com.myme.mywarehome.domains.issue.adapter.out;

import com.myme.mywarehome.domains.issue.adapter.out.event.TodayIssueEvent;
import com.myme.mywarehome.domains.issue.adapter.out.persistence.IssuePlanJpaRepository;
import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.in.command.GetAllIssuePlanCommand;
import com.myme.mywarehome.domains.issue.application.port.in.result.TodayIssueResult;
import com.myme.mywarehome.domains.issue.application.port.out.GetIssuePlanPort;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.myme.mywarehome.domains.receipt.adapter.out.event.TodayReceiptEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
@RequiredArgsConstructor
public class GetIssuePlanAdapter implements GetIssuePlanPort {
    private final IssuePlanJpaRepository issuePlanJpaRepository;
    private final Sinks.Many<TodayIssueEvent> sinks = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public Optional<IssuePlan> getIssuePlanById(Long issuePlanId) {
        return issuePlanJpaRepository.findById(issuePlanId);
    }

    @Override
    public Page<IssuePlan> findAllIssuePlans(GetAllIssuePlanCommand command, Pageable pageable) {
        return issuePlanJpaRepository.findByConditions(
                command.companyName(),
                command.productName(),
                command.companyCode(),
                command.issuePlanStartDate(),
                command.issuePlanEndDate(),
                command.productName(),
                command.issuePlanCode(),
                pageable
        );
    }

    @Override
    public boolean existsIssuePlanById(Long issuePlanId) {
        return issuePlanJpaRepository.existsById(issuePlanId);
    }


    @Override
    public List<IssuePlan> findAllIssuePlansByDate(LocalDate selectedDate) {
        return issuePlanJpaRepository.findByIssuePlanDate(selectedDate);
    }

    @Override
    public Page<TodayIssueResult> findTodayIssues(LocalDate today, Pageable pageable) {
        return issuePlanJpaRepository.findTodayIssues(
                today, pageable
        );
    }

    @Override
    public Optional<TodayIssueResult> findTodayIssueById(Long issueId, LocalDate selectedDate) {
        return issuePlanJpaRepository.findTodayIssueById(issueId, selectedDate);
    }

    @Override
    public Flux<ServerSentEvent<Object>> subscribeTodayIssues(LocalDate selectedDate, int page, int size) {
        return Flux.defer(() -> {

            // 1. 초기 페이지 메타데이터
            ServerSentEvent<Object> initialEvent = ServerSentEvent.builder()
                    .event("initial")
                    .data(Map.of(
                            "date", selectedDate,
                            "pageNumber", page,
                            "pageSize", size
                    ))
                    .build();

            // 2. 실시간 업데이트 구독
            Flux<ServerSentEvent<Object>> updates = sinks.asFlux()
                    .filter(event -> event.date().equals(selectedDate))
                    .map(event -> ServerSentEvent.builder()
                            .event("update")
                            .data(event.data())
                            .build());

            return Flux.concat(
                    Flux.just(initialEvent),
                    updates
            );
        });
    }

    // 변경사항 발생 히 호출할 메서드
    @Override
    public void emitTodayIssueUpdate(TodayIssueResult updatedResult, LocalDate selectedDate) {
        sinks.tryEmitNext(new TodayIssueEvent("UPDATE", updatedResult, selectedDate));

    }


}
