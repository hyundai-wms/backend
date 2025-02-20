package com.myme.mywarehome.domains.mrp.application.port.in;

import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecordItem;
import com.myme.mywarehome.domains.mrp.application.port.in.command.GetInventoryRecordItemCommand;
import com.myme.mywarehome.domains.mrp.application.port.in.result.GetInventoryRecordItemResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetInventoryRecordItemUseCase {
  //  Page<InventoryRecordItem> getInventoryRecordItem (GetAllInventoryRecordCommand command, Pageable pageable);
  Page<InventoryRecordItem> getInventoryRecordItem(GetInventoryRecordItemCommand command, Pageable pageable);
}
