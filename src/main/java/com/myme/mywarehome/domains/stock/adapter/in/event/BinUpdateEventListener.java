package com.myme.mywarehome.domains.stock.adapter.in.event;

import com.myme.mywarehome.domains.stock.adapter.in.event.event.BayBulkUpdateEvent;
import com.myme.mywarehome.domains.stock.adapter.in.event.event.BayUpdateEvent;
import com.myme.mywarehome.domains.stock.application.port.in.GetBayUseCase;
import com.myme.mywarehome.domains.stock.application.port.in.result.BayWithStockBinResult;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BinUpdateEventListener {
    private final GetBayUseCase getBayUseCase;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBayUpdated(BayUpdateEvent event) {

        // 1. 변경된 아이템에 대한 BayWithStockBinResult 리스트 조회
        List<BayWithStockBinResult> updatedItem = getBayUseCase.getBayListByProductNumber(event.productNumber());

        // 2. 모든 구독자에게 아이템 정보 전송
        updatedItem.forEach(getBayUseCase::notifyBayUpdate);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBayBulkUpdated(BayBulkUpdateEvent event) {

        // 변경된 모든 아이템에 대한 BayWithStockBinResult 조회
        event.productNumberList().forEach(productNumber -> {
            List<BayWithStockBinResult> updatedResult = getBayUseCase.getBayListByProductNumber(productNumber);
            updatedResult.forEach(getBayUseCase::notifyBayUpdate);
        });
    }
}
