package com.myme.mywarehome.domains.receipt.application.port.in.command;

import java.util.Map;

public record ReceiptProcessBulkCommand(
        Map<String, Double> productReturnRate
) {

}
