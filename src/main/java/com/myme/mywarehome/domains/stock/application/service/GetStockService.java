package com.myme.mywarehome.domains.stock.application.service;

import com.myme.mywarehome.domains.stock.application.domain.Stock;
import com.myme.mywarehome.domains.stock.application.port.in.GetStockUseCase;
import com.myme.mywarehome.domains.stock.application.port.out.GetStockPort;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetStockService implements GetStockUseCase {
    private final GetStockPort getStockPort;

    @Override
    public Page<Stock> getStockList(String productNumber, Pageable pageable, LocalDate selectedDate) {
        return getStockPort.findByProductNumber(productNumber, pageable, selectedDate);
    }
}
