package com.myme.mywarehome.domains.mrp.adapter.out;

import com.myme.mywarehome.domains.mrp.adapter.out.persistence.MrpOutputJpaRepository;
import com.myme.mywarehome.domains.mrp.application.domain.MrpOutput;
import com.myme.mywarehome.domains.mrp.application.exception.MrpCannotOrderException;
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

    @Override
    public void orderSuccess(MrpOutput mrpOutput) {
        if (mrpOutput.getMrpOutputId() == null) {
            throw new MrpCannotOrderException();
        }

        mrpOutputJpaRepository.save(mrpOutput);
    }
}
