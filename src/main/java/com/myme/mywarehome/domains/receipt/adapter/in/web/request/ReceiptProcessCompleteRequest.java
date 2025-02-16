package com.myme.mywarehome.domains.receipt.adapter.in.web.request;

import com.myme.mywarehome.domains.receipt.application.port.in.command.ReceiptProcessBulkCommand;
import com.myme.mywarehome.infrastructure.util.helper.DateFormatHelper;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.Map;

public record ReceiptProcessCompleteRequest(
        @NotNull(message = "반품률 Map은 필수입니다.")
        Map<String, Double> returnRate,

        @Pattern(regexp = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])", message = "날짜 형식이 올바르지 않습니다")
        String selectedDate
) {
    public ReceiptProcessBulkCommand toCommand() {
        LocalDate today;
        if(selectedDate == null || selectedDate.isBlank()) {
            today = LocalDate.now();
        } else {
            today = DateFormatHelper.parseDate(selectedDate);
        }

        return new ReceiptProcessBulkCommand(
                returnRate,
                today
        );
    }
}
