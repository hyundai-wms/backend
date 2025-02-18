package com.myme.mywarehome.domains.mrp.adapter.in.web.request;

import com.myme.mywarehome.domains.mrp.application.port.in.command.GetInventoryRecordItemCommand;

public record GetInventoryRecordItemRequest(
        String productNumber,
        String productName
) {
    public GetInventoryRecordItemCommand toCommand(Long inventoryRecordId) {
        return new GetInventoryRecordItemCommand(
                inventoryRecordId,
                this.productNumber,
                this.productName);
    }

}
