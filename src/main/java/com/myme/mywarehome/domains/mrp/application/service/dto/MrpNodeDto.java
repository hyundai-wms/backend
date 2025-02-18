package com.myme.mywarehome.domains.mrp.application.service.dto;

import com.myme.mywarehome.domains.product.application.domain.Product;

public record MrpNodeDto(
        Product product,
        long requiredPartsCount
) {
}
