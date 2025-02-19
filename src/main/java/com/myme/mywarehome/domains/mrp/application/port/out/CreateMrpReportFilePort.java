package com.myme.mywarehome.domains.mrp.application.port.out;

import com.myme.mywarehome.domains.mrp.application.domain.MrpExceptionReport;
import com.myme.mywarehome.domains.mrp.application.domain.MrpOutput;
import com.myme.mywarehome.domains.mrp.application.domain.ProductionPlanningReport;
import com.myme.mywarehome.domains.mrp.application.domain.PurchaseOrderReport;
import java.util.List;

public interface CreateMrpReportFilePort {
    String createAndUploadPurchaseOrderReport(MrpOutput mrpOutput, List<PurchaseOrderReport> reports);
    String createAndUploadProductionPlanningReport(MrpOutput mrpOutput, List<ProductionPlanningReport> reports);
    String createAndUploadMrpExceptionReport(MrpOutput mrpOutput, List<MrpExceptionReport> reports);
}
