package com.myme.mywarehome.domains.mrp.application.service.dto;

import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecordItem;
import java.time.LocalDate;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MrpContextDto {
    private final Map<Long, InventoryRecordItem> inventoryRecord;
    private LocalDate computedDate;

    public void updateComputedDate(LocalDate computedDate) {
        this.computedDate = computedDate;
    }
}
