package com.myme.mywarehome.domains.receipt.application.port.in.command;

import java.time.LocalDate;
import java.util.Map;

public record ReceiptProcessBulkCommand(
        Map<String, Double> productReturnRate,
        LocalDate selectedDate
) {

}
