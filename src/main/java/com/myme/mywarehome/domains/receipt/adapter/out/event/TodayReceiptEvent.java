package com.myme.mywarehome.domains.receipt.adapter.out.event;

import com.myme.mywarehome.domains.receipt.application.port.in.result.TodayReceiptResult;

import java.time.LocalDate;

public record TodayReceiptEvent(
        String type,
        TodayReceiptResult data,
        LocalDate date
) {

}

