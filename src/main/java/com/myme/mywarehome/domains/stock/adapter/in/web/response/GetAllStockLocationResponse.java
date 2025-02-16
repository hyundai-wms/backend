package com.myme.mywarehome.domains.stock.adapter.in.web.response;

import com.myme.mywarehome.domains.stock.application.port.in.result.BayWithStockBin;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;

public record GetAllStockLocationResponse(
        Map<Long, GetStockLocationResponse> content,
        Integer pageNumber,
        Integer pageSize,
        Long totalElements,
        Integer totalPages,
        Boolean isFirst,
        Boolean isLast
) {
    public static GetAllStockLocationResponse from(Page<BayWithStockBin> bayWithStockBinList) {
        return new GetAllStockLocationResponse(
                bayWithStockBinList.getContent().stream()
                        .collect(Collectors.toMap(
                                BayWithStockBin::bayId,
                                GetStockLocationResponse::from,
                                (a,b) -> b,
                                TreeMap::new
                        )),
                bayWithStockBinList.getNumber(),
                bayWithStockBinList.getSize(),
                bayWithStockBinList.getTotalElements(),
                bayWithStockBinList.getTotalPages(),
                bayWithStockBinList.isFirst(),
                bayWithStockBinList.isLast()
        );
    }
}
