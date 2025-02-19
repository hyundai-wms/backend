package com.myme.mywarehome.domains.mrp.application.port.out;

import com.myme.mywarehome.domains.mrp.application.domain.MrpOutput;

import java.util.Optional;

public interface GetMrpOutputPort {
    Optional<MrpOutput> getMrpOutputByMrpOutputId(Long mrpOutputId);
}
