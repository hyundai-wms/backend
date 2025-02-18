package com.myme.mywarehome.domains.mrp.application.port.out;

import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecord;
import com.myme.mywarehome.domains.mrp.application.port.in.command.GetAllInventoryRecordCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetAllInventoryRecordPort {
    Page<InventoryRecord> findAllInventoryRecords (GetAllInventoryRecordCommand command, Pageable pageable);

}
