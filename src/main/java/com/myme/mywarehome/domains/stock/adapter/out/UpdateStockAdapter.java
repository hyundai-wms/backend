package com.myme.mywarehome.domains.stock.adapter.out;

import com.myme.mywarehome.domains.stock.adapter.out.persistence.StockJpaRepository;
import com.myme.mywarehome.domains.stock.application.domain.Stock;
import com.myme.mywarehome.domains.stock.application.port.out.UpdateStockPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateStockAdapter implements UpdateStockPort {
    private final StockJpaRepository stockJpaRepository;

    @Override
    public Stock update(Stock stock) {
        return stockJpaRepository.save(stock);
    }
}
