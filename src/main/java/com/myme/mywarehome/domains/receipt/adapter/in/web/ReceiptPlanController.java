package com.myme.mywarehome.domains.receipt.adapter.in.web;

import com.myme.mywarehome.domains.receipt.adapter.in.web.request.CreateReceiptPlanRequest;
import com.myme.mywarehome.domains.receipt.adapter.in.web.request.GetAllReceiptPlanRequest;
import com.myme.mywarehome.domains.receipt.adapter.in.web.request.UpdateReceiptPlanRequest;
import com.myme.mywarehome.domains.receipt.adapter.in.web.response.GetAllReceiptPlanResponse;
import com.myme.mywarehome.domains.receipt.adapter.in.web.response.ReceiptPlanResponse;
import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.port.in.*;
import com.myme.mywarehome.domains.receipt.application.port.in.command.ReceiptPlanCommand;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/storages/receipts/plans")
@RequiredArgsConstructor
public class ReceiptPlanController {
    private final GetAllReceiptPlanUseCase getAllReceiptPlanUseCase;
    private final CreateReceiptPlanUseCase createReceiptPlanUseCase;
    private final UpdateReceiptPlanUseCase updateReceiptPlanUseCase;
    private final DeleteReceiptPlanUseCase deleteReceiptPlanUseCase;

    @GetMapping
    public CommonResponse<GetAllReceiptPlanResponse> getAllReceiptPlans(
            @Valid GetAllReceiptPlanRequest request,
            @PageableDefault(sort = "receiptPlanDate", direction = Direction.DESC) Pageable pageable
    ) {
        return CommonResponse.from(
                GetAllReceiptPlanResponse.from(
                        getAllReceiptPlanUseCase.getAllReceiptPlan(request.toCommand(), pageable)
                )
        );
    }

    @PostMapping
    public CommonResponse<ReceiptPlanResponse> createReceiptPlan(
            @Valid @RequestBody CreateReceiptPlanRequest request) {
        return CommonResponse.from(
                ReceiptPlanResponse.from(
                        createReceiptPlanUseCase.createReceiptPlan(request.toCommand()))
        );
    }

    // todo : 추후 MRP 시스템에서 발생한 Event를 Listening하도록 수정해야 함
    @PostMapping("/bulk")
    public CommonResponse<List<ReceiptPlanResponse>> createReceiptPlanBulk(
            @Valid @RequestBody List<CreateReceiptPlanRequest> requestList
    ) {
        // todo : 변환 책임이 원래 이 곳에 있으면 안됨. 나중에 API가 삭제될 예정이므로 당장은 여기서 구현
        List<ReceiptPlanCommand> commandList = requestList
                .stream()
                .map(CreateReceiptPlanRequest::toCommand)
                .toList();

        List<ReceiptPlan> result = createReceiptPlanUseCase.createReceiptPlanBulk(commandList);

        return CommonResponse.from(
                result.stream()
                        .map(ReceiptPlanResponse::from)
                        .toList()
        );
    }

    @PutMapping("/{receiptPlanId}")
    public CommonResponse<ReceiptPlanResponse> updateReceiptPlan(
            @PathVariable Long receiptPlanId,
            @Valid @RequestBody UpdateReceiptPlanRequest request
    ) {
        return CommonResponse.from(
                ReceiptPlanResponse.from(updateReceiptPlanUseCase.updateReceiptPlan(receiptPlanId,
                        request.toCommand()))
        );
    }

    @DeleteMapping("/{receiptPlanId}")
    public CommonResponse<Void> deleteReceiptPlan(
            @PathVariable Long receiptPlanId
    ) {
        deleteReceiptPlanUseCase.deleteReceiptPlan(receiptPlanId);
        return CommonResponse.empty();
    }
}
