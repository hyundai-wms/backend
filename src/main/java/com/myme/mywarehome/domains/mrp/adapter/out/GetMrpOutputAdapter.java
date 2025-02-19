package com.myme.mywarehome.domains.mrp.adapter.out;

import com.myme.mywarehome.domains.mrp.adapter.out.persistence.MrpOutputJpaRepository;
import com.myme.mywarehome.domains.mrp.application.domain.MrpOutput;
import com.myme.mywarehome.domains.mrp.application.port.out.GetMrpOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetMrpOutputAdapter implements GetMrpOutputPort {
    private final MrpOutputJpaRepository mrpOutputJpaRepository;

    @Override
    public Optional<MrpOutput> getMrpOutputByMrpOutputId(Long mrpOutputId) {
        Optional<MrpOutput> resultWithPurchase = mrpOutputJpaRepository.findByMrpOutputIdWithPurchaseOrders(mrpOutputId);
        if (resultWithPurchase.isEmpty()) {
            return Optional.empty();
        }

        Optional<MrpOutput> resultWithProduction = mrpOutputJpaRepository.findByMrpOutputIdWithProductionPlans(mrpOutputId);
        if (resultWithProduction.isEmpty()) {
            return Optional.empty();
        }

        MrpOutput output = resultWithPurchase.get();
        output.assignWithProductionPlanningReports(resultWithProduction.get().getProductionPlanningReportList());

        return Optional.of(output);
    }
}
