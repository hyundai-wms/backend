package com.myme.mywarehome.domains.mrp.application.port.out;

import com.myme.mywarehome.domains.mrp.application.domain.MrpOutput;
import com.myme.mywarehome.domains.mrp.application.port.in.command.GetAllMrpOutputCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetAllMrpOutputPort {
    Page<MrpOutput> findAllMrpOutputs (GetAllMrpOutputCommand command, Pageable pageable);

}
