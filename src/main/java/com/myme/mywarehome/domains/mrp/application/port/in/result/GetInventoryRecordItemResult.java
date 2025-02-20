package com.myme.mywarehome.domains.mrp.application.port.in.result;

import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecordItem;
import org.springframework.data.domain.Page;

public record GetInventoryRecordItemResult(Page<InventoryRecordItem> itemList) {

}
