package com.myme.mywarehome.domains.mrp.application.service;

import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecord;
import com.myme.mywarehome.domains.mrp.application.port.in.GetAllInventoryRecordUseCase;
import com.myme.mywarehome.domains.mrp.application.port.in.command.GetAllInventoryRecordCommand;
import com.myme.mywarehome.domains.mrp.application.port.out.GetAllInventoryRecordPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllInventoryRecordService implements GetAllInventoryRecordUseCase {
    private final GetAllInventoryRecordPort getAllInventoryRecordPort;

    @Override
    public Page<InventoryRecord> findAllInventoryRecord(GetAllInventoryRecordCommand command, Pageable pageable) {
        return getAllInventoryRecordPort.findAllInventoryRecords(command, pageable);
    }

}
