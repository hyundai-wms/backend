package com.myme.mywarehome.domains.mrp.application.service;

import com.myme.mywarehome.domains.mrp.application.domain.MrpOutput;
import com.myme.mywarehome.domains.mrp.application.port.in.MrpOutputUseCase;
import com.myme.mywarehome.domains.mrp.application.port.out.CreateMrpOutputPort;
import com.myme.mywarehome.domains.mrp.application.service.dto.MrpCalculateResultDto;
import com.myme.mywarehome.infrastructure.util.helper.DateFormatHelper;
import java.time.LocalDate;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MrpOutputService implements MrpOutputUseCase {
    private final CreateMrpOutputPort createMrpOutputPort;

    @Override
    public void saveResults(MrpCalculateResultDto result) {
        String createdDate = DateFormatHelper.formatDate(LocalDate.now());

        if (result.hasExceptions()) {
            // 예외가 있는 경우
            MrpOutput mrpOutput = MrpOutput.builder()
                    .createdDate(createdDate)
                    .orderedDate(LocalDate.now())
                    .canOrder(false)
                    .build();

            // 연관관계 설정
            mrpOutput.assignWithMrpExceptionReports(result.mrpExceptionReports());

            createMrpOutputPort.createMrpOutput(
                    mrpOutput,
                    Collections.emptyList(),
                    Collections.emptyList(),
                    result.mrpExceptionReports()
            );
        } else {
            // 정상적인 경우
            MrpOutput mrpOutput = MrpOutput.builder()
                    .createdDate(createdDate)
                    .orderedDate(LocalDate.now())
                    .canOrder(true)
                    .build();

            mrpOutput.assignWithPurchaseOrderReports(result.purchaseOrderReports());
            mrpOutput.assignWithProductionPlanningReports(result.productionPlanningReports());

            createMrpOutputPort.createMrpOutput(
                    mrpOutput,
                    result.purchaseOrderReports(),
                    result.productionPlanningReports(),
                    Collections.emptyList()
            );
        }
    }
}
