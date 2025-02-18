package com.myme.mywarehome.domains.mrp.application.service.dto;

import com.myme.mywarehome.domains.mrp.application.domain.MrpExceptionReport;
import com.myme.mywarehome.domains.mrp.application.domain.ProductionPlanningReport;
import com.myme.mywarehome.domains.mrp.application.domain.PurchaseOrderReport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record MrpCalculateResultDto(
        long nextRequiredPartsCount,
        List<PurchaseOrderReport> purchaseOrderReports,
        List<ProductionPlanningReport> productionPlanningReports,
        List<MrpExceptionReport> mrpExceptionReports
) {
    public static MrpCalculateResultDto withException(MrpExceptionReport exceptionReport) {
        return new MrpCalculateResultDto(
                -1,
                new ArrayList<>(),
                new ArrayList<>(),
                Collections.singletonList(exceptionReport)
        );
    }

    public boolean hasExceptions() {
        return !mrpExceptionReports.isEmpty();
    }
}
