package com.myme.mywarehome.domains.receipt.adapter.in.web;

import com.myme.mywarehome.domains.receipt.adapter.in.web.request.CreateReceiptPlanRequest;
import com.myme.mywarehome.domains.receipt.adapter.in.web.response.CreateReceiptPlanResponse;
import com.myme.mywarehome.domains.receipt.application.port.in.CreateReceiptPlanUseCase;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/storages/receipts/plans")
@RequiredArgsConstructor
public class ReceiptPlanController {
    private final CreateReceiptPlanUseCase createReceiptPlanUseCase;

    @PostMapping
    public CommonResponse<CreateReceiptPlanResponse> createReceiptPlan(
            @Valid @RequestBody CreateReceiptPlanRequest request) {
        return CommonResponse.from(
                CreateReceiptPlanResponse.from(
                        createReceiptPlanUseCase.createReceiptPlan(request.toCommand()))
        );
    }
}
