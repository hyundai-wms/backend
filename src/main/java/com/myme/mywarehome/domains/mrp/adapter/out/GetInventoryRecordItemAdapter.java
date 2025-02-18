package com.myme.mywarehome.domains.mrp.adapter.out;
import com.myme.mywarehome.domains.mrp.adapter.out.persistence.InventoryRecordItemJpaRepository;
import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecordItem;
import com.myme.mywarehome.domains.mrp.application.port.in.command.GetInventoryRecordItemCommand;
import com.myme.mywarehome.domains.mrp.application.port.out.GetInventoryRecordItemPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetInventoryRecordItemAdapter implements GetInventoryRecordItemPort {
    private final InventoryRecordItemJpaRepository inventoryRecordItemJpaRepository;

    @Override
    public Page<InventoryRecordItem> findInventoryRecordItems(GetInventoryRecordItemCommand command, Pageable pageable) {
        return inventoryRecordItemJpaRepository.findByConditions(
                command.inventoryRecordId(),
                command.productNumber(),
                command.productName(),
                pageable
        );
    }
}
