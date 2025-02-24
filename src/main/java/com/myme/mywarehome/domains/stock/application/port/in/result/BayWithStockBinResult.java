package com.myme.mywarehome.domains.stock.application.port.in.result;

import lombok.Builder;

@Builder
public record BayWithStockBinResult(
        Long bayId,
        String bayNumber,
        String productNumber,
        Long stockCount
) {

}
