package com.myme.mywarehome.domains.mrp.application.port.in;

import com.myme.mywarehome.domains.mrp.application.domain.MrpOutput;
import com.myme.mywarehome.domains.mrp.application.port.in.command.GetAllMrpOutputCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetAllMrpOutputUseCase {
    Page<MrpOutput> findAllMrpOutput(GetAllMrpOutputCommand command, Pageable pageable);

}
