package com.myme.mywarehome.domains.stock.application.port.out;

import com.myme.mywarehome.domains.stock.application.domain.Stock;
import com.myme.mywarehome.domains.stock.application.port.in.command.StockSummaryCommand;
import com.myme.mywarehome.domains.stock.application.port.in.result.StockSummaryResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

public interface GetStockPort {
    Optional<Stock> findById(Long stockId);
    Page<StockSummaryResult> findStockSummaries(StockSummaryCommand command, Pageable pageable, LocalDate selectedDate);
}
