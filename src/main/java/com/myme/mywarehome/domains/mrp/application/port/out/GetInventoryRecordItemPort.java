package com.myme.mywarehome.domains.mrp.application.port.out;

import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecordItem;
import com.myme.mywarehome.domains.mrp.application.port.in.command.GetInventoryRecordItemCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetInventoryRecordItemPort {
    Page<InventoryRecordItem> findInventoryRecordItems(GetInventoryRecordItemCommand command, Pageable pageable);

}
