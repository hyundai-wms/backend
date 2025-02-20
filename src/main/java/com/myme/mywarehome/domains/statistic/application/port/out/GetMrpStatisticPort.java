package com.myme.mywarehome.domains.statistic.application.port.out;

import com.myme.mywarehome.domains.statistic.adapter.in.web.response.GetMrpStatisticResponse;
import com.myme.mywarehome.domains.statistic.adapter.in.web.response.GetMrpStatisticResponse.CompanyReturnCount;
import java.util.List;

public interface GetMrpStatisticPort {
    List<CompanyReturnCount> getTop5ReturnCounts();
    Integer getTotalReturnCount();
    List<GetMrpStatisticResponse.CompanyProductCount> getTop5ProductCounts();

}
