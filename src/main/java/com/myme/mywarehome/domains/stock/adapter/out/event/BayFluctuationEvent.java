package com.myme.mywarehome.domains.stock.adapter.out.event;

import com.myme.mywarehome.domains.stock.application.port.in.result.BayWithStockBinResult;
import lombok.Builder;

@Builder
public record BayFluctuationEvent(
        String type,
        BayWithStockBinResult data
) {
}
