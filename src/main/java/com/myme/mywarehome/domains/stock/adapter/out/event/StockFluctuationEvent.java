package com.myme.mywarehome.domains.stock.adapter.out.event;

import com.myme.mywarehome.domains.stock.application.port.in.result.StockSummaryResult;

public record StockFluctuationEvent(
        String type,
        StockSummaryResult data
) {}
