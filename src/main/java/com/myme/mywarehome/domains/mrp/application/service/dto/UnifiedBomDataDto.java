package com.myme.mywarehome.domains.mrp.application.service.dto;

import com.myme.mywarehome.domains.mrp.application.domain.BomTree;
import com.myme.mywarehome.domains.product.application.domain.Product;
import java.util.List;
import java.util.Map;

public record UnifiedBomDataDto(
        Product virtualRoot,
        List<BomTree> unifiedBomTree,
        Map<Long, List<BomTree>> bomTreeMap
) {

}
