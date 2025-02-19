package com.myme.mywarehome.domains.mrp.adapter.in.web.request;

import com.myme.mywarehome.domains.mrp.application.port.in.command.GetAllMrpOutputCommand;
import com.myme.mywarehome.infrastructure.util.helper.DateFormatHelper;
import jakarta.validation.constraints.Pattern;

public record GetAllMrpOutputRequest(
        @Pattern(regexp = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])", message = "날짜 형식이 올바르지 않습니다")
        String startDate,
        @Pattern(regexp = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])", message = "날짜 형식이 올바르지 않습니다")
        String endDate,
        Boolean isOrdered
) {
    public GetAllMrpOutputCommand toCommand() {
        return new GetAllMrpOutputCommand(
                this.startDate == null ? null
                        : DateFormatHelper.parseDate(this.startDate),
                this.endDate == null ? null
                        : DateFormatHelper.parseDate(this.endDate),
                this.isOrdered
        );
    }


}
