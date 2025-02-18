package com.myme.mywarehome.domains.mrp.application.port.in;

import com.myme.mywarehome.domains.mrp.application.service.dto.MrpCalculateResultDto;
import com.myme.mywarehome.domains.mrp.application.service.dto.MrpContextDto;
import com.myme.mywarehome.domains.mrp.application.service.dto.MrpNodeDto;
import java.time.LocalDate;

public interface MrpCalculatorUseCase {
    MrpCalculateResultDto calculate(MrpNodeDto mrpNode, MrpContextDto context);
}
