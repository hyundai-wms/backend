package com.myme.mywarehome.domains.stock.adapter.out;

import com.myme.mywarehome.domains.stock.adapter.out.persistence.StockJpaRepository;
import com.myme.mywarehome.domains.stock.adapter.out.persistence.StockMybatisRepository;
import com.myme.mywarehome.domains.stock.application.domain.Stock;
import com.myme.mywarehome.domains.stock.application.port.in.command.StockSummaryCommand;
import com.myme.mywarehome.domains.stock.application.port.in.result.StockSummaryResult;
import com.myme.mywarehome.domains.stock.application.port.out.GetStockPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetStockAdapter implements GetStockPort {
    private final StockJpaRepository stockJpaRepository;
    private final StockMybatisRepository stockMybatisRepository;

    @Override
    public Optional<Stock> findById(Long stockId) {
        return stockJpaRepository.findById(stockId);
    }

    @Override
    public Page<StockSummaryResult> findStockSummaries(StockSummaryCommand command, Pageable pageable, LocalDate selectedDate) {
        return stockMybatisRepository.findStockSummaries(command, pageable, selectedDate);
    }

}
