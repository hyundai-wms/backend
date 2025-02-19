package com.myme.mywarehome.domains.product.adapter.in.event;

import com.myme.mywarehome.domains.mrp.application.port.in.event.UpdateSafetyStockFromMrpEvent;
import com.myme.mywarehome.domains.product.application.port.in.UpdateSafeItemCountUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MrpSafeItemCountEventListener {
    private final UpdateSafeItemCountUseCase updateSafeItemCountUseCase;

    @Async
    @TransactionalEventListener
    public void handleUpdateSafeItem(UpdateSafetyStockFromMrpEvent event) {
        updateSafeItemCountUseCase.updateAllSafeItemCount(event.toCommands());
    }
}
