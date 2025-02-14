package com.myme.mywarehome.domains.stock.application.port.out;

import com.myme.mywarehome.domains.stock.application.domain.Stock;

import java.util.List;

public interface CreateStockPort {
    Stock create(Stock stock);
    void createBulk(List<Stock> stocks);
}
