package com.myme.mywarehome.domains.stock.adapter.in.web.response;

import com.myme.mywarehome.domains.stock.application.port.in.result.BinInfoResult;
import com.myme.mywarehome.infrastructure.util.helper.DateFormatHelper;

public record GetBinResponse(
        String productNumber,
        String productName,
        Integer binLocation,
        String binName,
        Long itemId,
        String itemCode,
        Long receiptId,
        String receiptCode,
        String receiptDate,
        Long companyId,
        String companyCode,
        String companyName
) {
    public static GetBinResponse from(BinInfoResult binInfoResult) {
        return new GetBinResponse(
                binInfoResult.productNumber(),
                binInfoResult.productName(),
                binInfoResult.binLocation(),
                String.format("%s-%02d", binInfoResult.bayNumber(), binInfoResult.binLocation()),
                binInfoResult.stockId(),
                binInfoResult.stockCode(),
                binInfoResult.receiptId(),
                binInfoResult.receiptCode(),
                DateFormatHelper.formatDate(binInfoResult.receiptDate()),
                binInfoResult.companyId(),
                binInfoResult.companyCode(),
                binInfoResult.companyName()
        );
    }
}
