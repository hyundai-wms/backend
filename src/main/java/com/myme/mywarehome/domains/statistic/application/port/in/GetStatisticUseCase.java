package com.myme.mywarehome.domains.statistic.application.port.in;

import com.myme.mywarehome.domains.statistic.adapter.in.web.response.GetStatisticResponse;
import java.time.LocalDate;

public interface GetStatisticUseCase {
    GetStatisticResponse getStatistic(LocalDate selectedDate);
}


