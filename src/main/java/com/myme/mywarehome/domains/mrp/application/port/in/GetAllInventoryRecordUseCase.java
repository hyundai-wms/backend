package com.myme.mywarehome.domains.mrp.application.port.in;

import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecord;
import com.myme.mywarehome.domains.mrp.application.port.in.command.GetAllInventoryRecordCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetAllInventoryRecordUseCase {
    Page<InventoryRecord> findAllInventoryRecord (GetAllInventoryRecordCommand command, Pageable pageable);

}
