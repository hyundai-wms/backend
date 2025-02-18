package com.myme.mywarehome.domains.mrp.application.port.in;

import com.myme.mywarehome.domains.mrp.application.service.dto.MrpCalculateResultDto;

public interface MrpOutputUseCase {
    void saveResults(MrpCalculateResultDto result);
}
