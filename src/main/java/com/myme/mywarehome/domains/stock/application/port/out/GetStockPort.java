package com.myme.mywarehome.domains.stock.application.port.out;

import com.myme.mywarehome.domains.stock.application.domain.Stock;

import java.util.Optional;

public interface GetStockPort {
    Optional<Stock> findById(Long stockId);

}
