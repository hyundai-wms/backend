package com.myme.mywarehome.domains.receipt.adapter.in.web.request;

import com.myme.mywarehome.domains.receipt.application.port.in.command.ReceiptProcessBulkCommand;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record ReceiptProcessCompleteRequest(
        @NotNull(message = "반품률 Map은 필수입니다.")
        Map<String, Double> returnRate
) {
    public ReceiptProcessBulkCommand toCommand() {
        return new ReceiptProcessBulkCommand(
                returnRate
        );
    }
}
