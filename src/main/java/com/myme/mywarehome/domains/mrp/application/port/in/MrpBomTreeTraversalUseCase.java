package com.myme.mywarehome.domains.mrp.application.port.in;

import com.myme.mywarehome.domains.mrp.application.port.in.command.MrpInputCommand;
import com.myme.mywarehome.domains.mrp.application.service.dto.MrpCalculateResultDto;
import com.myme.mywarehome.domains.mrp.application.service.dto.MrpContextDto;
import com.myme.mywarehome.domains.mrp.application.service.dto.UnifiedBomDataDto;
import java.time.LocalDate;

public interface MrpBomTreeTraversalUseCase {
    MrpCalculateResultDto traverse(MrpInputCommand command, UnifiedBomDataDto unifiedBomData, MrpContextDto context);
}
