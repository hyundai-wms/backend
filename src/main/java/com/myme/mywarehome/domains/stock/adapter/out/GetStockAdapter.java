package com.myme.mywarehome.domains.stock.adapter.out;

import com.myme.mywarehome.domains.stock.adapter.out.persistence.StockJpaRepository;
import com.myme.mywarehome.domains.stock.application.domain.Stock;
import com.myme.mywarehome.domains.stock.application.port.out.GetStockPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetStockAdapter implements GetStockPort {
    private final StockJpaRepository stockJpaRepository;

    @Override
    public Optional<Stock> findById(Long stockId) {
        return stockJpaRepository.findById(stockId);
    }


}
