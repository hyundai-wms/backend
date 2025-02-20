package com.myme.mywarehome.domains.statistic.application.service;

import com.myme.mywarehome.domains.statistic.adapter.in.web.response.GetMrpStatisticResponse;
import com.myme.mywarehome.domains.statistic.application.port.in.GetMrpStatisticUseCase;
import com.myme.mywarehome.domains.statistic.application.port.out.GetMrpStatisticPort;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetMrpStatisticService implements GetMrpStatisticUseCase {
    private final GetMrpStatisticPort getMrpStatisticPort;

    @Override
    public GetMrpStatisticResponse getMrpStatistic() {
        return GetMrpStatisticResponse.of(
                getMrpStatisticPort.getTop5ReturnCounts(),
                getMrpStatisticPort.getTotalReturnCount(),
                getMrpStatisticPort.getTop5ProductCounts()
        );
    }
}
