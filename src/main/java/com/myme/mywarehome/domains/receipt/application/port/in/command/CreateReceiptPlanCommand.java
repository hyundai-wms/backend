package com.myme.mywarehome.domains.receipt.application.port.in.command;

import java.time.LocalDate;

public record CreateReceiptPlanCommand(
        String productNumber,
        Integer itemCount,
        LocalDate receiptPlanDate
) {

}
