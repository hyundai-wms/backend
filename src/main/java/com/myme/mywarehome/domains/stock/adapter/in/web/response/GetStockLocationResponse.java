package com.myme.mywarehome.domains.stock.adapter.in.web.response;

import com.myme.mywarehome.domains.stock.application.port.in.result.BayWithStockBinResult;

public record GetStockLocationResponse(
        String bayNumber,
        String productNumber,
        Long itemCount
) {
    public static GetStockLocationResponse from(BayWithStockBinResult bayWithStockBin) {
        return new GetStockLocationResponse(
                bayWithStockBin.bayNumber(),
                bayWithStockBin.productNumber(),
                bayWithStockBin.stockCount()
        );
    }
}
