package com.myme.mywarehome.domains.stock.application.service;

import com.myme.mywarehome.domains.stock.application.domain.Stock;
import com.myme.mywarehome.domains.stock.application.port.in.GetSpecificStockUseCase;
import com.myme.mywarehome.domains.stock.application.port.out.GetStockPort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetSpecificStockService implements GetSpecificStockUseCase {
    private final GetStockPort getStockPort;

    @Override
    public Optional<Stock> getSpecificStock(Long stockId) {
        return getStockPort.findById(stockId);
    }
}
