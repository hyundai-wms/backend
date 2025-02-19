package com.myme.mywarehome.domains.product.application.service;

import com.myme.mywarehome.domains.product.application.port.in.UpdateSafeItemCountUseCase;
import com.myme.mywarehome.domains.product.application.port.in.command.UpdateSafeItemCountCommand;
import com.myme.mywarehome.domains.product.application.port.out.UpdateProductPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UpdateSafeItemCountService implements UpdateSafeItemCountUseCase {
    private final UpdateProductPort updateProductPort;

    @Override
    public void updateAllSafeItemCount(List<UpdateSafeItemCountCommand> commands) {
        updateProductPort.updateAllSafeItemCount(commands);
    }
}
