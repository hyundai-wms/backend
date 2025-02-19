package com.myme.mywarehome.domains.mrp.application.service;

import com.myme.mywarehome.domains.mrp.application.domain.MrpOutput;
import com.myme.mywarehome.domains.mrp.application.port.in.GetAllMrpOutputUseCase;
import com.myme.mywarehome.domains.mrp.application.port.in.command.GetAllMrpOutputCommand;
import com.myme.mywarehome.domains.mrp.application.port.out.GetAllMrpOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllMrpOutputService implements GetAllMrpOutputUseCase {
    private final GetAllMrpOutputPort getAllMrpOutputPort;

    @Override
    public Page<MrpOutput> findAllMrpOutput(GetAllMrpOutputCommand command, Pageable pageable) {
        return getAllMrpOutputPort.findAllMrpOutputs(command, pageable);
    }
}
