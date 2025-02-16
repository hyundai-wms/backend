package com.myme.mywarehome.domains.stock.adapter.in.web.response;

import com.myme.mywarehome.domains.stock.application.port.in.result.BayWithStockBin;
import java.util.List;

public record GetStockLocationByProductNumberResponse(
        List<BayWithStockBin> content,
        Integer nonZeroBayCount,
        Integer bayCount,
        Long itemCount
) {
    public static GetStockLocationByProductNumberResponse from(List<BayWithStockBin> getStockLocationResponseList) {
        return new GetStockLocationByProductNumberResponse(
                getStockLocationResponseList,
                (int) getStockLocationResponseList.stream()
                        .map(BayWithStockBin::stockCount)
                        .filter(it -> it > 0)
                        .count(),
                getStockLocationResponseList.size(),
                getStockLocationResponseList.stream()
                        .map(BayWithStockBin::stockCount)
                        .reduce(0L, Long::sum)
        );
    }
}
