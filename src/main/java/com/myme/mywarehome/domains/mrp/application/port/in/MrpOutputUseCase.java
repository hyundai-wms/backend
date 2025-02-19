package com.myme.mywarehome.domains.mrp.application.port.in;

import com.myme.mywarehome.domains.mrp.application.port.in.command.MrpInputCommand;
import com.myme.mywarehome.domains.mrp.application.service.dto.MrpCalculateResultDto;

import java.time.LocalDate;

public interface MrpOutputUseCase {
    void saveResults(MrpInputCommand command, MrpCalculateResultDto result);
}
