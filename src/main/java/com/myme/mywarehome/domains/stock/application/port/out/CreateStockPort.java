package com.myme.mywarehome.domains.stock.application.port.out;

import com.myme.mywarehome.domains.stock.application.domain.Stock;

public interface CreateStockPort {
    Stock create(Stock stock);
}
