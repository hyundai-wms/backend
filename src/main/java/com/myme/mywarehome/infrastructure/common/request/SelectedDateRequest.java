package com.myme.mywarehome.infrastructure.common.request;

import com.myme.mywarehome.infrastructure.util.helper.DateFormatHelper;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record SelectedDateRequest(
        @Pattern(regexp = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])", message = "날짜 형식이 올바르지 않습니다")
        String selectedDate
) {
    public static LocalDate toLocalDate(SelectedDateRequest selectedDateRequest) {
        if(selectedDateRequest == null) {
            return LocalDate.now();
        } else {
            return selectedDateRequest.toLocalDate();
        }
    }

    private LocalDate toLocalDate() {
        if(selectedDate==null || selectedDate.isBlank()) {
            return LocalDate.now();
        } else {
            return DateFormatHelper.parseDate(selectedDate);
        }
    }
}
