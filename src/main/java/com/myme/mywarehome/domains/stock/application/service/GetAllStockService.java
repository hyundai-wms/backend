package com.myme.mywarehome.domains.stock.application.service;

import com.myme.mywarehome.domains.stock.application.port.in.GetAllStockUseCase;
import com.myme.mywarehome.domains.stock.application.port.in.command.StockSummaryCommand;
import com.myme.mywarehome.domains.stock.application.port.in.result.StockSummaryResult;
import com.myme.mywarehome.domains.stock.application.port.out.GetStockPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class GetAllStockService implements GetAllStockUseCase {
    private final GetStockPort getStockPort;

    @Override
    public Page<StockSummaryResult> getAllStockList(StockSummaryCommand command, Pageable pageable, LocalDate selectedDate) {
        return getStockPort.findStockSummaries(command, pageable, selectedDate);
    }

    @Override
    public StockSummaryResult getStockByProductNumber(String productNumber) {
        return getStockPort.findStockSummaryByProductNumber(productNumber)
                .orElseGet(() -> StockSummaryResult.builder().build());
    }

    @Override
    public Flux<ServerSentEvent<Object>> subscribeStockFluctuation(StockSummaryCommand command,
            Pageable pageable, LocalDate selectedDate) {
        return getStockPort.subscribeStockFluctuation(command, pageable, selectedDate);
    }

    @Override
    public void notifyStockUpdate(StockSummaryResult stockSummaryResult) {
        getStockPort.emitStockUpdate(stockSummaryResult);
    }
}
