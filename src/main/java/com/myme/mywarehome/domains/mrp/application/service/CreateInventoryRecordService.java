package com.myme.mywarehome.domains.mrp.application.service;

import static java.util.stream.Collectors.toList;

import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecord;
import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecordItem;
import com.myme.mywarehome.domains.mrp.application.port.in.CreateInventoryRecordUseCase;
import com.myme.mywarehome.domains.mrp.application.port.in.result.ProductStockCount;
import com.myme.mywarehome.domains.mrp.application.port.out.CreateInventoryRecordPort;
import com.myme.mywarehome.domains.mrp.application.port.out.LoadProductWithStocksForInventoryRecordPort;
import com.myme.mywarehome.domains.product.application.domain.Product;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateInventoryRecordService implements CreateInventoryRecordUseCase {
    private final CreateInventoryRecordPort createInventoryRecordPort;
    private final LoadProductWithStocksForInventoryRecordPort loadProductWithStocksForInventoryRecordPort;

    @Override
    @Transactional
    public void createInventoryRecord() {
        // 1. InventoryRecord 생성
        InventoryRecord inventoryRecord = InventoryRecord.builder()
                .stockStatusAt(LocalDateTime.now())
                .build();

        // 2. 전체 물품 조회
        List<ProductStockCount> productStockCountList = loadProductWithStocksForInventoryRecordPort.loadAllProductsWithAvailableStocks();

        // 3. 물품 각각에 대하여 InventoryRecordItem 생성
        List<InventoryRecordItem> inventoryRecordItemList = productStockCountList.stream()
                .map(productStockCount ->
                    InventoryRecordItem.builder()
                            .inventoryRecord(inventoryRecord)
                            .product(Product.builder()  // Product 엔티티 참조만 생성
                                    .productId(productStockCount.productId())
                                    .build())
                            .stockCount(productStockCount.stockCount())
                            .compositionRatio(productStockCount.compositionRatio())
                            .leadTime(productStockCount.leadTime())
                            .build()
                )
                .toList();

        // 4. 저장
        createInventoryRecordPort.createInventoryRecord(inventoryRecord, inventoryRecordItemList);
    }
}
