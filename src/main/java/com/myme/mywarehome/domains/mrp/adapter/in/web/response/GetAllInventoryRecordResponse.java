package com.myme.mywarehome.domains.mrp.adapter.in.web.response;

import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecord;
import com.myme.mywarehome.infrastructure.util.helper.DateFormatHelper;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;

public record GetAllInventoryRecordResponse(
        List<InventoryRecordInfo> content,
        Integer pageNumber,
        Integer pageSize,
        Long totalElements,
        Integer totalPages,
        Boolean isFirst,
        Boolean isLast
) {

    public record InventoryRecordInfo(
            Long inventoryRecordId,
            String inventoryRecordCode,
            String createdDate,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {

        public static InventoryRecordInfo from(InventoryRecord record) {
            return new InventoryRecordInfo(
                    record.getInventoryRecordId(),
                    record.getInventoryRecordCode(),
                    DateFormatHelper.formatDate(record.getStockStatusAt().toLocalDate()),
                    record.getCreatedAt(),
                    record.getUpdatedAt()
            );
        }
    }

    public static GetAllInventoryRecordResponse from(Page<InventoryRecord> page) {
        return new GetAllInventoryRecordResponse(
                page.getContent().stream()
                        .map(InventoryRecordInfo::from)
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
