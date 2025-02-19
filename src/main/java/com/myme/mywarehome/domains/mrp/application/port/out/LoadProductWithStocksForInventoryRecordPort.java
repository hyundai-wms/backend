package com.myme.mywarehome.domains.mrp.application.port.out;

import com.myme.mywarehome.domains.mrp.application.port.in.result.ProductStockCount;
import java.util.List;

public interface LoadProductWithStocksForInventoryRecordPort {
    List<ProductStockCount> loadAllProductsWithAvailableStocks();
}
