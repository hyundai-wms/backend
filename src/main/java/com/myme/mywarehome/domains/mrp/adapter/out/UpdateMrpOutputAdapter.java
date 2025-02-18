package com.myme.mywarehome.domains.mrp.adapter.out;

import com.myme.mywarehome.domains.mrp.adapter.out.persistence.MrpOutputJpaRepository;
import com.myme.mywarehome.domains.mrp.application.port.out.UpdateMrpOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateMrpOutputAdapter implements UpdateMrpOutputPort {
    private final MrpOutputJpaRepository mrpOutputJpaRepository;

    @Override
    public void deactivatePreviousOrders() {
        mrpOutputJpaRepository.deactivateAllPreviousOrders();
    }
}
