package com.myme.mywarehome.domains.product.application.port.in.command;

public record UpdateSafeItemCountCommand(
        String productNumber,
        Integer safeItemCount
) {
}
