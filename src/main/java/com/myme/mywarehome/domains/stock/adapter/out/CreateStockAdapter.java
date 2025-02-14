package com.myme.mywarehome.domains.stock.adapter.out;

import com.myme.mywarehome.domains.stock.adapter.out.persistence.StockJpaRepository;
import com.myme.mywarehome.domains.stock.application.domain.Stock;
import com.myme.mywarehome.domains.stock.application.port.out.CreateStockPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CreateStockAdapter implements CreateStockPort {
    private final StockJpaRepository stockJpaRepository;

    @Override
    public Stock create(Stock stock) {
        return stockJpaRepository.save(stock);
    }

    @Override
    public void createBulk(List<Stock> stocks) {
        stockJpaRepository.saveAll(stocks);
    }
}
