package com.myme.mywarehome.domains.stock.adapter.in.event.event;

import java.util.List;

public record StockBulkUpdateEvent(
        List<String> productNumberList
) {

}
