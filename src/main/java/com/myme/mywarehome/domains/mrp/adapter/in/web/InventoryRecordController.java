package com.myme.mywarehome.domains.mrp.adapter.in.web;

import com.myme.mywarehome.domains.mrp.application.port.in.CreateInventoryRecordUseCase;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/productions/inventory-records")
@RequiredArgsConstructor
public class InventoryRecordController {
    private final CreateInventoryRecordUseCase createInventoryRecordUseCase;

    @PostMapping
    public CommonResponse<Void> createInventoryRecord() {
        createInventoryRecordUseCase.createInventoryRecord();
        return CommonResponse.empty();
    }
}
