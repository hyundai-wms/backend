package com.myme.mywarehome.domains.stock.adapter.in.web.response;

import com.myme.mywarehome.domains.stock.application.domain.Stock;
import java.util.List;
import org.springframework.data.domain.Page;

public record GetStockResponse(
        List<GetStockDetailResponse> content,
        Integer pageNumber,
        Integer pageSize,
        Long totalElements,
        Integer totalPages,
        Boolean isFirst,
        Boolean isLast
) {
    public static GetStockResponse from(Page<Stock> stockList) {
        return new GetStockResponse(
                stockList.getContent().stream()
                        .map(GetStockDetailResponse::from)
                        .toList(),
                stockList.getNumber(),
                stockList.getSize(),
                stockList.getTotalElements(),
                stockList.getTotalPages(),
                stockList.isFirst(),
                stockList.isLast()
        );
    }
}
