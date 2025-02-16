package com.myme.mywarehome.domains.stock.adapter.in.web.response;

import com.myme.mywarehome.domains.stock.application.port.in.result.BinInfoResult;
import java.util.List;

public record GetBayResponse(
        List<GetBinResponse> content,
        Integer itemCount
) {
    public static GetBayResponse from(List<BinInfoResult> binInfoResultList) {
        return new GetBayResponse(
                binInfoResultList.stream()
                        .map(GetBinResponse::from)
                        .toList(),
                (int) binInfoResultList.stream()
                        .filter(it -> it.stockId() != null)
                        .count()
        );
    }
}
