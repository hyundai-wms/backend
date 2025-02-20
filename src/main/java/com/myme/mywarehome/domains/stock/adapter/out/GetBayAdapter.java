package com.myme.mywarehome.domains.stock.adapter.out;

import com.myme.mywarehome.domains.stock.adapter.out.event.BayFluctuationEvent;
import com.myme.mywarehome.domains.stock.adapter.out.event.StockFluctuationEvent;
import com.myme.mywarehome.domains.stock.adapter.out.persistence.BayJpaRepository;
import com.myme.mywarehome.domains.stock.application.domain.Bay;
import com.myme.mywarehome.domains.stock.application.port.in.result.BayWithStockBinResult;
import com.myme.mywarehome.domains.stock.application.port.in.result.BinInfoResult;
import com.myme.mywarehome.domains.stock.application.port.out.GetBayPort;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
@RequiredArgsConstructor
public class GetBayAdapter implements GetBayPort {
    public final BayJpaRepository bayJpaRepository;
    private final Sinks.Many<BayFluctuationEvent> sinks = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public Page<BayWithStockBinResult> getAllBayList(Pageable pageable) {
        return bayJpaRepository.findAllBaysWithStockCount(pageable);
    }

    @Override
    public List<BayWithStockBinResult> getAllBayByProductNumber(String productNumber) {
        return bayJpaRepository.findAllBaysByProductNumberWithStockCount(productNumber);
    }

    @Override
    public List<BinInfoResult> getBayByBayNumber(String bayNumber) {
        return bayJpaRepository.findByBayNumber(bayNumber);
    }

    @Override
    public Flux<ServerSentEvent<Object>> subscribeBayFluctuation() {
        return Flux.defer(() -> {
            // 1. 초기 페이지 메타데이터
            ServerSentEvent<Object> initialEvent = ServerSentEvent.builder()
                    .event("initial")
                    .data("")
                    .build();

            // 2. 실시간 업데이트 구독
            Flux<ServerSentEvent<Object>> updates = sinks.asFlux()
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
    public void emitBayUpdate(BayWithStockBinResult bayWithStockBinResult) {
        sinks.tryEmitNext(new BayFluctuationEvent(
                "UPDATE",
                bayWithStockBinResult
        ));
    }

}
