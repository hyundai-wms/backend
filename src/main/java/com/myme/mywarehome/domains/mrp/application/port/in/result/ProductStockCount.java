package com.myme.mywarehome.domains.mrp.application.port.in.result;

public record ProductStockCount(
        Long productId,
        String productNumber,
        String productName,
        Integer compositionRatio,
        Integer leadTime,
        Long stockCount
) {

}
