package com.myme.mywarehome.domains.mrp.adapter.out;

import com.myme.mywarehome.domains.mrp.adapter.out.persistence.MrpExceptionReportJpaRepository;
import com.myme.mywarehome.domains.mrp.adapter.out.persistence.MrpOutputJpaRepository;
import com.myme.mywarehome.domains.mrp.adapter.out.persistence.ProductionPlanningReportJpaRepository;
import com.myme.mywarehome.domains.mrp.adapter.out.persistence.PurchaseOrderReportJpaRepository;
import com.myme.mywarehome.domains.mrp.application.domain.MrpExceptionReport;
import com.myme.mywarehome.domains.mrp.application.domain.MrpOutput;
import com.myme.mywarehome.domains.mrp.application.domain.ProductionPlanningReport;
import com.myme.mywarehome.domains.mrp.application.domain.PurchaseOrderReport;
import com.myme.mywarehome.domains.mrp.application.port.out.CreateMrpOutputPort;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateMrpOutputAdapter implements CreateMrpOutputPort {
    private final MrpOutputJpaRepository mrpOutputJpaRepository;
    private final PurchaseOrderReportJpaRepository purchaseOrderReportJpaRepository;
    private final ProductionPlanningReportJpaRepository productionPlanningReportJpaRepository;
    private final MrpExceptionReportJpaRepository mrpExceptionReportJpaRepository;

    @Override
    @Transactional
    public void createMrpOutput(MrpOutput mrpOutput, List<PurchaseOrderReport> purchaseOrderReports,
            List<ProductionPlanningReport> productionPlanningReports,
            List<MrpExceptionReport> mrpExceptionReports) {

        // 1. MrpOutput 저장
        mrpOutputJpaRepository.save(mrpOutput);

        // 2. 각 보고서 저장
        if (!purchaseOrderReports.isEmpty()) {
            purchaseOrderReportJpaRepository.saveAll(purchaseOrderReports);
        }

        if (!productionPlanningReports.isEmpty()) {
            productionPlanningReportJpaRepository.saveAll(productionPlanningReports);
        }

        if (!mrpExceptionReports.isEmpty()) {
            mrpExceptionReportJpaRepository.saveAll(mrpExceptionReports);
        }

    }
}
