package com.myme.mywarehome.domains.statistic.adapter;

import com.myme.mywarehome.domains.statistic.adapter.in.web.response.GetStatisticResponse;
import com.myme.mywarehome.domains.statistic.application.port.in.GetStatisticUseCase;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import com.myme.mywarehome.infrastructure.config.resolver.SelectedDate;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/statistics/storages")
@RequiredArgsConstructor
public class StatisticController {
    private final GetStatisticUseCase getStatisticUseCase;

    @GetMapping
    public CommonResponse<GetStatisticResponse> getStatistic(@SelectedDate LocalDate selectedDate) {
        return CommonResponse.from(
                getStatisticUseCase.getStatistic(selectedDate)
        );
    }

}
