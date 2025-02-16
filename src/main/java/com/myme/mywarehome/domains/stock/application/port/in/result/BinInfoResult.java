package com.myme.mywarehome.domains.stock.application.port.in.result;

import java.time.LocalDate;

public record BinInfoResult(
        String bayNumber,
        String productNumber,
        String productName,
        Integer binLocation,
        Long stockId,
        String stockCode,
        Long receiptId,
        String receiptCode,
        LocalDate receiptDate,
        Long companyId,
        String companyCode,
        String companyName
) {
}
