package com.myme.mywarehome.domains.mrp.application.port.in;

import com.myme.mywarehome.domains.mrp.application.port.in.command.MrpInputCommand;

public interface MrpOperationUseCase {
    void run(MrpInputCommand command);
}
