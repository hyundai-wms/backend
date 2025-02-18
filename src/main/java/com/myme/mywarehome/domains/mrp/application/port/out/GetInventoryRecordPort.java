package com.myme.mywarehome.domains.mrp.application.port.out;

import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecordItem;
import java.util.List;

public interface GetInventoryRecordPort {
    List<InventoryRecordItem> findRecentInventoryRecord();
}
