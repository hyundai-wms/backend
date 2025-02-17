package com.myme.mywarehome.domains.stock.application.port.in;

import com.myme.mywarehome.domains.stock.application.domain.Stock;
import java.util.Optional;

public interface GetSpecificStockUseCase {
    Optional<Stock> getSpecificStock(Long stockId);

}
