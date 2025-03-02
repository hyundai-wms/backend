package com.myme.mywarehome.domains.mrp.adapter.in.web.response;

import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecordItem;
import java.util.List;
import org.springframework.data.domain.Page;

public record GetInventoryRecordItemResponse(
        List<InventoryRecordItemInfo> content,
        Integer pageNumber,
        Integer pageSize,
        long totalElements,
        Integer totalPages,
        boolean isFirst,
        boolean isLast
) {
    public record InventoryRecordItemInfo(
            String applicableEngine,
            String productNumber,
            String productName,
            Integer compositionRatio,
            Long itemCount,
            Integer leadTime
    ) {
        public static InventoryRecordItemInfo from(InventoryRecordItem item) {
            return new InventoryRecordItemInfo(
                    item.getProduct().getApplicableEngine(),
                    item.getProduct().getProductNumber(),
                    item.getProduct().getProductName(),
                    item.getCompositionRatio(),
                    item.getStockCount(),
                    item.getProduct().getLeadTime()
            );
        }
    }
    public static GetInventoryRecordItemResponse of(Page<InventoryRecordItem> page) {
        return new GetInventoryRecordItemResponse(
                page.getContent().stream()
                        .map(InventoryRecordItemInfo::from)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

}
