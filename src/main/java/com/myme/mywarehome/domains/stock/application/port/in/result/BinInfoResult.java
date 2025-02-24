package com.myme.mywarehome.domains.stock.application.port.in.result;

import lombok.Builder;

import java.time.LocalDate;

@Builder
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
