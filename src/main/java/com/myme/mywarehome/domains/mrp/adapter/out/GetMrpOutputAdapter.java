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
        return mrpOutputJpaRepository.findById(mrpOutputId);
    }
}
