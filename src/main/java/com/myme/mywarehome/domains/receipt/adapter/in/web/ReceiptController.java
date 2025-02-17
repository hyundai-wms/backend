package com.myme.mywarehome.domains.receipt.adapter.in.web;

import com.myme.mywarehome.domains.receipt.adapter.in.web.request.GetAllReceiptRequest;
import com.myme.mywarehome.domains.receipt.adapter.in.web.request.ReceiptProcessCompleteRequest;
import com.myme.mywarehome.domains.receipt.adapter.in.web.response.GetAllReceiptResponse;
import com.myme.mywarehome.domains.receipt.adapter.in.web.response.ReceiptProcessResponse;
import com.myme.mywarehome.domains.receipt.adapter.in.web.response.TodayReceiptResponse;
import com.myme.mywarehome.domains.receipt.application.port.in.GetAllReceiptUseCase;
import com.myme.mywarehome.domains.receipt.application.port.in.GetTodayReceiptUseCase;
import com.myme.mywarehome.domains.receipt.application.port.in.ReceiptProcessUseCase;
import com.myme.mywarehome.domains.receipt.application.port.in.ReceiptReturnUseCase;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import com.myme.mywarehome.infrastructure.config.resolver.SelectedDate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/v1/storages/receipts")
@RequiredArgsConstructor
public class ReceiptController {
    private final ReceiptProcessUseCase receiptProcessedUseCase;
    private final ReceiptReturnUseCase receiptReturnUseCase;
    private final GetAllReceiptUseCase getAllReceiptUseCase;
    private final GetTodayReceiptUseCase getTodayReceiptUseCase;

    @GetMapping
    public CommonResponse<GetAllReceiptResponse> getAllReceipts(
            @Valid GetAllReceiptRequest request,
            @PageableDefault @SortDefault.SortDefaults({
                    @SortDefault(sort = "receiptDate", direction = Direction.DESC),
                    @SortDefault(sort = "receiptId", direction = Direction.ASC)
            }) Pageable pageable
    ) {
        return CommonResponse.from(
                GetAllReceiptResponse.from(
                        getAllReceiptUseCase.getAllReceipt(request.toCommand(), pageable)
                )
        );
    }

    @GetMapping("/today")
    public CommonResponse<TodayReceiptResponse> getTodayReceipts(
            @SelectedDate LocalDate selectedDate,
            @PageableDefault Pageable pageable
    ) {
        return CommonResponse.from(
                TodayReceiptResponse.from(
                        getTodayReceiptUseCase.getTodayReceipt(selectedDate, pageable)
                )
        );
    }

    @PostMapping("/{outboundProductId}/items")
    public CommonResponse<ReceiptProcessResponse> receiptProcessed(
            @PathVariable("outboundProductId") String outboundProductId,
            @SelectedDate LocalDate selectedDate
    ) {
        return CommonResponse.from(
                ReceiptProcessResponse.from(
                        receiptProcessedUseCase.process(outboundProductId, selectedDate)
                )
        );
    }

    @PostMapping("/{outboundProductId}/returns")
    public CommonResponse<Void> returnProcessed(
            @PathVariable("outboundProductId") String outboundProductId,
            @SelectedDate LocalDate selectedDate
    ) {
        receiptReturnUseCase.process(outboundProductId, selectedDate);
        return CommonResponse.empty();
    }

    @PostMapping("/complete")
    public CommonResponse<Void> processCompleted(
            @Valid @RequestBody ReceiptProcessCompleteRequest request,
            @SelectedDate LocalDate selectedDate
    ) {
        receiptProcessedUseCase.processBulk(request.toCommand(), selectedDate);
        return CommonResponse.empty();
    }
}
