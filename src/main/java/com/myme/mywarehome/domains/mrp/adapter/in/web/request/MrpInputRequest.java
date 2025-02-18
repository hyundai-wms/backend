package com.myme.mywarehome.domains.mrp.adapter.in.web.request;

import com.myme.mywarehome.domains.mrp.application.port.in.command.MrpInputCommand;
import com.myme.mywarehome.infrastructure.util.helper.DateFormatHelper;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.Map;

public record MrpInputRequest(
        @NotNull(message = "kappa는 필수 값입니다")
        @Min(value = 0, message = "kappa는 0 이상의 값이어야 합니다")
        Integer kappa,

        @NotNull(message = "gamma는 필수 값입니다")
        @Min(value = 0, message = "gamma는 0 이상의 값이어야 합니다")
        Integer gamma,

        @NotNull(message = "nu는 필수 값입니다")
        @Min(value = 0, message = "nu는 0 이상의 값이어야 합니다")
        Integer nu,

        @NotNull(message = "theta는 필수 값입니다")
        @Min(value = 0, message = "theta는 0 이상의 값이어야 합니다")
        Integer theta,

        @NotBlank(message = "납기일은 필수입니다.")
        @Pattern(regexp = "\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12][0-9]|3[01])", message = "날짜 형식이 올바르지 않습니다")
        String dueDate
) {

    public MrpInputCommand toCommand() {
        Map<String, Integer> engineCountMap = new HashMap<>();
        engineCountMap.put("kappa", kappa);
        engineCountMap.put("gamma", gamma);
        engineCountMap.put("nu", nu);
        engineCountMap.put("theta", theta);
        return new MrpInputCommand(
                engineCountMap,
                DateFormatHelper.parseDate(dueDate)
        );
    }
}
