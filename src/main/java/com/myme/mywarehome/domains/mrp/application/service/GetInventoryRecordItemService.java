package com.myme.mywarehome.domains.mrp.application.service;

import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecordItem;
import com.myme.mywarehome.domains.mrp.application.port.in.GetInventoryRecordItemUseCase;
import com.myme.mywarehome.domains.mrp.application.port.in.command.GetInventoryRecordItemCommand;
import com.myme.mywarehome.domains.mrp.application.port.in.result.GetInventoryRecordItemResult;
import com.myme.mywarehome.domains.mrp.application.port.out.GetInventoryRecordItemPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetInventoryRecordItemService implements GetInventoryRecordItemUseCase {
    private final GetInventoryRecordItemPort getInventoryRecordItemPort;

    // 상세 정보 조회
    @Override
    public GetInventoryRecordItemResult getInventoryRecordItem(GetInventoryRecordItemCommand command, Pageable pageable) {
        Page<InventoryRecordItem> itemList = getInventoryRecordItemPort.findInventoryRecordItems(
                command, pageable);

        String applicableEngine = itemList.getContent().stream()
                .map(item -> item.getProduct().getApplicableEngine())
                .findFirst()
                .orElse("");

        return new GetInventoryRecordItemResult(applicableEngine, itemList);
    }

}
