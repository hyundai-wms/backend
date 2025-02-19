package com.myme.mywarehome.domains.mrp.adapter.out;

import com.myme.mywarehome.domains.mrp.adapter.out.persistence.MrpOutputJpaRepository;
import com.myme.mywarehome.domains.mrp.application.domain.MrpOutput;
import com.myme.mywarehome.domains.mrp.application.port.in.command.GetAllMrpOutputCommand;
import com.myme.mywarehome.domains.mrp.application.port.out.GetAllMrpOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetAllMrpOutputAdapter implements GetAllMrpOutputPort {
    private final MrpOutputJpaRepository mrpOutputJpaRepository;

    @Override
    public Page<MrpOutput> findAllMrpOutputs(GetAllMrpOutputCommand command, Pageable pageable) {
        return mrpOutputJpaRepository.findAllByConditions(
                command.startDate(),
                command.endDate(),
                command.isOrdered(),
                pageable
        );
    }
}
