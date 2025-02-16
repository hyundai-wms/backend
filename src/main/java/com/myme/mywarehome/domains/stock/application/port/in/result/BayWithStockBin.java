package com.myme.mywarehome.domains.stock.application.port.in.result;

public record BayWithStockBin(
        Long bayId,
        String bayNumber,
        String productNumber,
        Long stockCount
) {

}
