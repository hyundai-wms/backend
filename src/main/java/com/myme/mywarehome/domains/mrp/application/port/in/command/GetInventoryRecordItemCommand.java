package com.myme.mywarehome.domains.mrp.application.port.in.command;

public record GetInventoryRecordItemCommand(
        Long inventoryRecordId,
        String productNumber,
        String productName
) {

}
