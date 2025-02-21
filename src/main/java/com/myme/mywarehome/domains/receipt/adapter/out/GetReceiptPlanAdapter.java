package com.myme.mywarehome.domains.receipt.adapter.out;

import com.myme.mywarehome.domains.receipt.adapter.out.event.TodayReceiptEvent;
import com.myme.mywarehome.domains.receipt.adapter.out.persistence.ReceiptPlanJpaRepository;
import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.port.in.command.GetAllReceiptPlanCommand;
import com.myme.mywarehome.domains.receipt.application.port.in.result.TodayReceiptResult;
import com.myme.mywarehome.domains.receipt.application.port.out.GetReceiptPlanPort;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetReceiptPlanAdapter implements GetReceiptPlanPort {
    private final ReceiptPlanJpaRepository receiptPlanJpaRepository;
    private final Sinks.Many<TodayReceiptEvent> sinks = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public Optional<ReceiptPlan> findReceiptPlanById(Long receiptPlanId) {
        return receiptPlanJpaRepository.findById(receiptPlanId);
    }

    @Override
    public Page<ReceiptPlan> findAllReceiptPlans(GetAllReceiptPlanCommand command, Pageable pageable, LocalDate selectedDate) {
        return receiptPlanJpaRepository.findByConditions(
                command.companyName(),
                command.productName(),
                command.receiptPlanCode(),
                command.companyCode(),
                command.receiptPlanStartDate(),
                command.receiptPlanEndDate(),
                command.productNumber(),
                selectedDate,
                pageable);
    }

    @Override
    public boolean existsReceiptPlanById(Long receiptPlanId) {
        return receiptPlanJpaRepository.existsById(receiptPlanId);
    }

    @Override
    public List<ReceiptPlan> findAllReceiptPlansByDate(LocalDate selectedDate) {
        return receiptPlanJpaRepository.findByReceiptPlanDate(selectedDate);
    }

    @Override
    public Page<TodayReceiptResult> findTodayReceipts(LocalDate today, Pageable pageable) {
        return receiptPlanJpaRepository.findTodayReceipts(
                today,
                pageable
        );
    }

    @Override
    public Optional<TodayReceiptResult> findTodayReceiptById(Long receiptId, LocalDate selectedDate) {
        return receiptPlanJpaRepository.findTodayReceiptById(receiptId, selectedDate);
    }

    @Override
    public Flux<ServerSentEvent<Object>> subscribeTodayReceipts(LocalDate selectedDate, int page, int size) {
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
            ).onErrorContinue((err, obj) -> {
                log.error("Error in stream:", err);
                // 에러가 발생해도 스트림 유지
            });
        });
    }

    // 변경사항 발생 시 호출할 메서드
    @Override
    public void emitTodayReceiptUpdate(TodayReceiptResult updatedResult, LocalDate selectedDate) {
        sinks.tryEmitNext(new TodayReceiptEvent("UPDATE", updatedResult, selectedDate));
    }
}
