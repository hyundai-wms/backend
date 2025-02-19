package com.myme.mywarehome.domains.product.application.port.out;

import com.myme.mywarehome.domains.product.application.port.in.command.UpdateSafeItemCountCommand;

import java.util.List;

public interface UpdateProductPort {
    void updateAllSafeItemCount(List<UpdateSafeItemCountCommand> commands);
}
