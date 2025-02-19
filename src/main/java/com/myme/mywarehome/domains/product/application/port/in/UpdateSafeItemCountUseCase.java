package com.myme.mywarehome.domains.product.application.port.in;

import com.myme.mywarehome.domains.product.application.port.in.command.UpdateSafeItemCountCommand;

import java.util.List;

public interface UpdateSafeItemCountUseCase {
    void updateAllSafeItemCount(List<UpdateSafeItemCountCommand> commands);
}
