package com.myme.mywarehome.domains.stock.adapter.in.event.event;

import java.util.List;

public record BayBulkUpdateEvent(
        List<String> productNumberList
) {
}
