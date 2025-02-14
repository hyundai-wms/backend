package com.myme.mywarehome.domains.receipt.adapter.in.web;

import com.myme.mywarehome.domains.receipt.adapter.in.web.request.ReceiptOrReturnProcessRequest;
import com.myme.mywarehome.domains.receipt.adapter.in.web.request.ReceiptProcessCompleteRequest;
import com.myme.mywarehome.domains.receipt.adapter.in.web.response.ReceiptProcessResponse;
import com.myme.mywarehome.domains.receipt.application.port.in.ReceiptProcessUseCase;
import com.myme.mywarehome.domains.receipt.application.port.in.ReceiptReturnUseCase;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/storages/receipts")
@RequiredArgsConstructor
public class ReceiptController {
    private final ReceiptProcessUseCase receiptProcessedUseCase;
    private final ReceiptReturnUseCase receiptReturnUseCase;

    @PostMapping("/{outboundProductId}/items")
    public CommonResponse<ReceiptProcessResponse> receiptProcessed(
            @PathVariable("outboundProductId") String outboundProductId,
            @Valid @RequestBody ReceiptOrReturnProcessRequest request
    ) {
        return CommonResponse.from(
                ReceiptProcessResponse.from(
                        receiptProcessedUseCase.process(outboundProductId, request.toCommand())
                )
        );
    }

    @PostMapping("/{outboundProductId}/returns")
    public CommonResponse<Void> returnProcessed(
            @PathVariable("outboundProductId") String outboundProductId,
            @Valid @RequestBody ReceiptOrReturnProcessRequest request
    ) {
        receiptReturnUseCase.process(outboundProductId, request.toCommand());
        return CommonResponse.empty();
    }

    @PostMapping("/complete")
    public CommonResponse<Void> processCompleted(
            @Valid @RequestBody ReceiptProcessCompleteRequest request
    ) {
        receiptProcessedUseCase.processBulk(request.toCommand());
        return CommonResponse.empty();
    }
}
