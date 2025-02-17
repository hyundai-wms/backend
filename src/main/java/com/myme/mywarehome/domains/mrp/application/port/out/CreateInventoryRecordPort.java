package com.myme.mywarehome.domains.mrp.application.port.out;

import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecord;
import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecordItem;
import java.util.List;

public interface CreateInventoryRecordPort {
    void createInventoryRecord(
            InventoryRecord inventoryRecord, List<InventoryRecordItem> inventoryRecordItemList);
}
