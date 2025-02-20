package com.myme.mywarehome.domains.stock.application.port.in;

import com.myme.mywarehome.domains.stock.application.port.in.command.StockSummaryCommand;
import com.myme.mywarehome.domains.stock.application.port.in.result.StockSummaryResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface GetAllStockUseCase {
    Page<StockSummaryResult> getAllStockList(StockSummaryCommand command, Pageable pageable, LocalDate selectedDate);
    StockSummaryResult getStockByProductNumber(String productNumber);
    Flux<ServerSentEvent<Object>> subscribeStockFluctuation(StockSummaryCommand command, Pageable pageable, LocalDate selectedDate);
    void notifyStockUpdate(StockSummaryResult stockSummaryResult);
}
