package com.myme.mywarehome.domains.mrp.adapter.out;

import com.myme.mywarehome.domains.mrp.adapter.out.persistence.InventoryRecordItemJpaRepository;
import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecordItem;
import com.myme.mywarehome.domains.mrp.application.port.out.GetInventoryRecordPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetInventoryRecordAdapter implements GetInventoryRecordPort {
    private final InventoryRecordItemJpaRepository inventoryRecordItemJpaRepository;

    @Override
    public List<InventoryRecordItem> findRecentInventoryRecord() {
        return inventoryRecordItemJpaRepository.findRecentInventoryRecord();
    }
}
