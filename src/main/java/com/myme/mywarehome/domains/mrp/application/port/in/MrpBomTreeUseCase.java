package com.myme.mywarehome.domains.mrp.application.port.in;

import com.myme.mywarehome.domains.mrp.application.port.in.command.MrpInputCommand;
import com.myme.mywarehome.domains.mrp.application.service.dto.UnifiedBomDataDto;

public interface MrpBomTreeUseCase {

    UnifiedBomDataDto createUnifiedBomTree(MrpInputCommand command);
}
