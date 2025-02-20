package com.myme.mywarehome.domains.statistic.application.port.in;

import com.myme.mywarehome.domains.statistic.adapter.in.web.response.GetMrpStatisticResponse;
import java.time.LocalDate;

public interface GetMrpStatisticUseCase {
    GetMrpStatisticResponse getMrpStatistic();

}
