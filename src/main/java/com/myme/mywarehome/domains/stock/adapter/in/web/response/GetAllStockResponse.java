package com.myme.mywarehome.domains.stock.adapter.in.web.response;

import com.myme.mywarehome.domains.stock.application.port.in.result.StockSummaryResult;
import org.springframework.data.domain.Page;

import java.util.List;

public record GetAllStockResponse(
        List<StockSummaryResult> content,
        Integer pageNumber,
        Integer pageSize,
        Long totalElements,
        Integer totalPages,
        Boolean isFirst,
        Boolean isLast
) {
    public static GetAllStockResponse from(Page<StockSummaryResult> stockSummaryResults) {
        return new GetAllStockResponse(
                stockSummaryResults.getContent(),
                stockSummaryResults.getNumber(),
                stockSummaryResults.getSize(),
                stockSummaryResults.getTotalElements(),
                stockSummaryResults.getTotalPages(),
                stockSummaryResults.isFirst(),
                stockSummaryResults.isLast()
        );
    }
}
