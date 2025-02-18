package com.myme.mywarehome.domains.mrp.adapter.in.web;

import com.myme.mywarehome.domains.mrp.adapter.in.web.request.GetAllInventoryRecordRequest;
import com.myme.mywarehome.domains.mrp.adapter.in.web.response.GetAllInventoryRecordResponse;
import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecord;
import com.myme.mywarehome.domains.mrp.application.port.in.CreateInventoryRecordUseCase;
import com.myme.mywarehome.domains.mrp.application.port.in.GetAllInventoryRecordUseCase;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/productions/inventory-records")
@RequiredArgsConstructor
public class InventoryRecordController {
    private final CreateInventoryRecordUseCase createInventoryRecordUseCase;
    private final GetAllInventoryRecordUseCase getAllInventoryRecordUseCase;

    @PostMapping
    public CommonResponse<Void> createInventoryRecord() {
        createInventoryRecordUseCase.createInventoryRecord();
        return CommonResponse.empty();
    }

    @GetMapping
    public CommonResponse<GetAllInventoryRecordResponse> getAllInventoryRecords(
            @Valid GetAllInventoryRecordRequest request,
            @PageableDefault @SortDefault.SortDefaults({
                    @SortDefault(sort = "stockStatusAt", direction = Direction.DESC),
                    @SortDefault(sort = "inventoryRecordId", direction = Direction.ASC)
            }) Pageable pageable
    ) {
        return CommonResponse.from(GetAllInventoryRecordResponse.from(
                getAllInventoryRecordUseCase.findAllInventoryRecord(request.toCommand(), pageable)
            )
        );
    }
}
