package com.myme.mywarehome.domains.receipt.adapter.in.web.request;

import com.myme.mywarehome.domains.receipt.application.port.in.command.SelectedDateCommand;
import com.myme.mywarehome.infrastructure.util.helper.DateFormatHelper;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record SelectedDateRequest(
        @Pattern(regexp = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])", message = "날짜 형식이 올바르지 않습니다")
        String selectedDate
) {
    public static SelectedDateCommand toCommand(SelectedDateRequest request) {
        if (request == null) {
            return new SelectedDateCommand(LocalDate.now());
        }
        return request.toCommand();
    }

    public SelectedDateCommand toCommand() {
        LocalDate today;
        if(selectedDate == null || selectedDate.isBlank()) {
            today = LocalDate.now();
        } else {
            today = DateFormatHelper.parseDate(selectedDate);
        }

        return new SelectedDateCommand(today);
    }
}
