package com.myme.mywarehome.domains.stock.application.port.in;

import com.myme.mywarehome.domains.stock.application.domain.Stock;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetStockUseCase {
    Page<Stock> getStockList(String productNumber, Pageable pageable, LocalDate selectedDate);
}
