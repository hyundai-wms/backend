package com.myme.mywarehome.domains.mrp.application.service.dto;

import com.myme.mywarehome.domains.product.application.domain.Product;

public record MrpProblemNode(
        Product product,
        String exceptionType,
        String exceptionMessage,
        long requiredCount,
        int leadTimeDays,
        int availableBins
) {
}
