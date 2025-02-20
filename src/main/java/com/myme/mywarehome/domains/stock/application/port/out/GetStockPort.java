package com.myme.mywarehome.domains.stock.application.port.out;

import com.myme.mywarehome.domains.stock.application.domain.Stock;
import com.myme.mywarehome.domains.stock.application.port.in.command.StockSummaryCommand;
import com.myme.mywarehome.domains.stock.application.port.in.result.StockSummaryResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface GetStockPort {
    Optional<Stock> findById(Long stockId);
    Page<StockSummaryResult> findStockSummaries(StockSummaryCommand command, Pageable pageable, LocalDate selectedDate);
    Page<Stock> findByProductNumber(String productNumber, Pageable pageable, LocalDate selectedDate);
    Flux<ServerSentEvent<Object>> subscribeStockFluctuation(StockSummaryCommand command, Pageable pageable, LocalDate selectedDate);
    void emitStockUpdate(StockSummaryResult stockSummaryResult);
    Optional<StockSummaryResult> findStockSummaryByProductNumber(String productNumber);
}
