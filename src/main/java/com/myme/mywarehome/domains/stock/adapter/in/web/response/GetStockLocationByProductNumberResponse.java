package com.myme.mywarehome.domains.stock.adapter.in.web.response;

import com.myme.mywarehome.domains.stock.application.port.in.result.BayWithStockBinResult;
import java.util.List;

public record GetStockLocationByProductNumberResponse(
        List<BayWithStockBinResult> content,
        Integer nonZeroBayCount,
        Integer bayCount,
        Long itemCount
) {
    public static GetStockLocationByProductNumberResponse from(List<BayWithStockBinResult> getStockLocationResponseList) {
        return new GetStockLocationByProductNumberResponse(
                getStockLocationResponseList,
                (int) getStockLocationResponseList.stream()
                        .map(BayWithStockBinResult::stockCount)
                        .filter(it -> it > 0)
                        .count(),
                getStockLocationResponseList.size(),
                getStockLocationResponseList.stream()
                        .map(BayWithStockBinResult::stockCount)
                        .reduce(0L, Long::sum)
        );
    }
}
