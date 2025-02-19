package com.myme.mywarehome.domains.mrp.application.port.out;

import com.myme.mywarehome.domains.mrp.application.domain.MrpExceptionReport;
import com.myme.mywarehome.domains.mrp.application.domain.MrpOutput;
import com.myme.mywarehome.domains.mrp.application.domain.ProductionPlanningReport;
import com.myme.mywarehome.domains.mrp.application.domain.PurchaseOrderReport;
import java.util.List;

public interface CreateMrpOutputPort {
    void createMrpOutput(
            MrpOutput mrpOutput,
            List<PurchaseOrderReport> purchaseOrderReports,
            List<ProductionPlanningReport> productionPlanningReports,
            List<MrpExceptionReport> mrpExceptionReports
    );
}
