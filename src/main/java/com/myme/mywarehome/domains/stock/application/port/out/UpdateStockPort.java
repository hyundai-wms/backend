package com.myme.mywarehome.domains.stock.application.port.out;

import com.myme.mywarehome.domains.stock.application.domain.Stock;

public interface UpdateStockPort {
    Stock update(Stock stock);
}
