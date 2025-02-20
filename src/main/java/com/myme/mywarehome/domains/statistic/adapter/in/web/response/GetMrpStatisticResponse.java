package com.myme.mywarehome.domains.statistic.adapter.in.web.response;

import java.util.List;

public record GetMrpStatisticResponse(
        List<CompanyReturnCount> returnCount,
        Integer totalReturnCount,
        List<CompanyProductCount> productCount
) {
    public record CompanyReturnCount(
            String companyName,
            Integer returnCount
    ) {}

    public record CompanyProductCount(
            String companyName,
            Integer receiptCount
    ) {}

    public static GetMrpStatisticResponse of(
            List<CompanyReturnCount> returnCount,
            Integer totalReturnCount,
            List<CompanyProductCount> productCount
    ) {
        return new GetMrpStatisticResponse(returnCount, totalReturnCount, productCount);
    }

}
