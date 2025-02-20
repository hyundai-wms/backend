package com.myme.mywarehome.domains.stock.adapter.out;

import com.myme.mywarehome.domains.stock.adapter.out.event.StockFluctuationEvent;
import com.myme.mywarehome.domains.stock.adapter.out.persistence.StockJpaRepository;
import com.myme.mywarehome.domains.stock.adapter.out.persistence.StockMybatisRepository;
import com.myme.mywarehome.domains.stock.application.domain.Stock;
import com.myme.mywarehome.domains.stock.application.port.in.command.StockSummaryCommand;
import com.myme.mywarehome.domains.stock.application.port.in.result.StockSummaryResult;
import com.myme.mywarehome.domains.stock.application.port.out.GetStockPort;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetStockAdapter implements GetStockPort {
    private final StockJpaRepository stockJpaRepository;
    private final StockMybatisRepository stockMybatisRepository;
    private final Sinks.Many<StockFluctuationEvent> sinks = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public Optional<Stock> findById(Long stockId) {
        return stockJpaRepository.findById(stockId);
    }

    @Override
    public Page<StockSummaryResult> findStockSummaries(StockSummaryCommand command, Pageable pageable, LocalDate selectedDate) {
        return stockMybatisRepository.findStockSummaries(command, pageable, selectedDate);
    }

    @Override
    public Page<Stock> findByProductNumber(String productNumber, Pageable pageable,
            LocalDate selectedDate) {
        return stockJpaRepository.findByProductNumber(productNumber, selectedDate, pageable);
    }

    @Override
    public Flux<ServerSentEvent<Object>> subscribeStockFluctuation(StockSummaryCommand command,
            Pageable pageable, LocalDate selectedDate) {
        return Flux.defer(() -> {
            // 1. 초기 페이지 메타데이터
            ServerSentEvent<Object> initialEvent = ServerSentEvent.builder()
                    .event("initial")
                    .data(Map.of(
                            "date", selectedDate,
                            "pageNumber", pageable.getPageNumber(),
                            "pageSize", pageable.getPageSize(),
                            "option", command
                    ))
                    .build();

            // 2. 실시간 업데이트 구독
            Flux<ServerSentEvent<Object>> updates = sinks.asFlux()
                    // 커맨드 조건에 맞는지 체크
                    .filter(event -> {
                        long position = stockMybatisRepository.findStockPosition(
                                event.data().productNumber(),
                                command,
                                selectedDate
                        );

                        long startIndex = (long) pageable.getPageNumber() * pageable.getPageSize();
                        long endIndex = startIndex + pageable.getPageSize();

                        return position >= startIndex && position < endIndex;
                    })
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

    @Override
    public void emitStockUpdate(StockSummaryResult stockSummaryResult) {
        sinks.tryEmitNext(new StockFluctuationEvent(
                "UPDATE",
                stockSummaryResult
        ));
    }

    @Override
    public Optional<StockSummaryResult> findStockSummaryByProductNumber(String productNumber) {
        return stockMybatisRepository.findOptionalStockSummaryByProductNumber(productNumber);
    }

}
