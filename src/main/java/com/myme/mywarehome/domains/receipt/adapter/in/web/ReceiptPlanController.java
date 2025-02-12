package com.myme.mywarehome.domains.receipt.adapter.in.web;

import com.myme.mywarehome.domains.receipt.adapter.in.web.request.CreateReceiptPlanRequest;
import com.myme.mywarehome.domains.receipt.adapter.in.web.response.CreateReceiptPlanResponse;
import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.port.in.CreateReceiptPlanUseCase;
import com.myme.mywarehome.domains.receipt.application.port.in.command.CreateReceiptPlanCommand;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import jakarta.validation.Valid;
import java.util.List;
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

    // todo : 추후 MRP 시스템에서 발생한 Event를 Listening하도록 수정해야 함
    @PostMapping("/bulk")
    public CommonResponse<List<CreateReceiptPlanResponse>> createReceiptPlanBulk(
            @Valid @RequestBody List<CreateReceiptPlanRequest> requestList
    ) {
        // todo : 변환 책임이 원래 이 곳에 있으면 안됨. 나중에 API가 삭제될 예정이므로 당장은 여기서 구현
        List<CreateReceiptPlanCommand> commandList = requestList
                .stream()
                .map(CreateReceiptPlanRequest::toCommand)
                .toList();

        List<ReceiptPlan> result = createReceiptPlanUseCase.createReceiptPlanBulk(commandList);

        return CommonResponse.from(
                result.stream()
                        .map(CreateReceiptPlanResponse::from)
                        .toList()
        );
    }

}
