package com.myme.mywarehome.domains.stock.application.port.in;

import com.myme.mywarehome.domains.receipt.application.domain.Receipt;
import com.myme.mywarehome.domains.stock.application.domain.Stock;

public interface CreateStockUseCase {
    Stock createStock(Receipt receipt);
}
