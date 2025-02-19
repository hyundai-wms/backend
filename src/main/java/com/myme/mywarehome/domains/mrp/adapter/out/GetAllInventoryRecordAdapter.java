package com.myme.mywarehome.domains.mrp.adapter.out;

import com.myme.mywarehome.domains.mrp.adapter.out.persistence.InventoryRecordJpaRepository;
import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecord;
import com.myme.mywarehome.domains.mrp.application.port.in.command.GetAllInventoryRecordCommand;
import com.myme.mywarehome.domains.mrp.application.port.out.GetAllInventoryRecordPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetAllInventoryRecordAdapter implements GetAllInventoryRecordPort {
    private final InventoryRecordJpaRepository inventoryRecordJpaRepository;

    @Override
    public Page<InventoryRecord> findAllInventoryRecords(GetAllInventoryRecordCommand command,
            Pageable pageable) {
        return inventoryRecordJpaRepository.findByConditions(
                command.startDate(),
                command.endDate(),
                pageable
        );
    }


}
