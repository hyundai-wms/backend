package com.myme.mywarehome.domains.mrp.adapter.out;

import com.myme.mywarehome.domains.mrp.application.port.in.result.ProductStockCount;
import com.myme.mywarehome.domains.mrp.application.port.out.LoadProductWithStocksForInventoryRecordPort;
import com.myme.mywarehome.domains.product.adapter.out.persistence.ProductJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoadProductWithStocksForInventoryRecordAdapter implements
        LoadProductWithStocksForInventoryRecordPort {
    private final ProductJpaRepository productJpaRepository;

    @Override
    public List<ProductStockCount> loadAllProductsWithAvailableStocks() {
        return productJpaRepository.findAllProductsWithAvailableStockCount();
    }
}
