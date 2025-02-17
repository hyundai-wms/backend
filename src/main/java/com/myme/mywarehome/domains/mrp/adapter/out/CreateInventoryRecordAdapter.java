package com.myme.mywarehome.domains.mrp.adapter.out;

import com.myme.mywarehome.domains.mrp.adapter.out.persistence.InventoryRecordItemJpaRepository;
import com.myme.mywarehome.domains.mrp.adapter.out.persistence.InventoryRecordJpaRepository;
import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecord;
import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecordItem;
import com.myme.mywarehome.domains.mrp.application.port.out.CreateInventoryRecordPort;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateInventoryRecordAdapter implements CreateInventoryRecordPort {
    private final InventoryRecordJpaRepository inventoryRecordJpaRepository;
    private final InventoryRecordItemJpaRepository inventoryRecordItemJpaRepository;

    @Override
    @Transactional
    public void createInventoryRecord(
            InventoryRecord inventoryRecord, List<InventoryRecordItem> inventoryRecordItemList) {
        // 1. InventoryRecord 저장
        inventoryRecordJpaRepository.save(inventoryRecord);

        // 2. InventoryRecordItem 저장
        inventoryRecordItemJpaRepository.saveAll(inventoryRecordItemList);
    }
}
