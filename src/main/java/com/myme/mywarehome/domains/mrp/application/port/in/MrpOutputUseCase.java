package com.myme.mywarehome.domains.mrp.application.port.in;

import com.myme.mywarehome.domains.mrp.application.service.dto.MrpCalculateResultDto;

import java.time.LocalDate;

public interface MrpOutputUseCase {
    void saveResults(LocalDate dueDate, MrpCalculateResultDto result);
}
