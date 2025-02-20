package com.myme.mywarehome.domains.stock.adapter.in.event;

import com.myme.mywarehome.domains.stock.adapter.in.event.event.StockBulkUpdateEvent;
import com.myme.mywarehome.domains.stock.adapter.in.event.event.StockUpdateEvent;
import com.myme.mywarehome.domains.stock.application.port.in.GetAllStockUseCase;
import com.myme.mywarehome.domains.stock.application.port.in.result.StockSummaryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class StockUpdateEventListener {
    private final GetAllStockUseCase getAllStockUseCase;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleStockUpdated(StockUpdateEvent stockUpdateEvent) {

        // 1. 변경된 아이템에 대한 StockSummaryResult 조회
        StockSummaryResult updatedItem = getAllStockUseCase.getStockByProductNumber(stockUpdateEvent.productNumber());

        // 2. 모든 구독자에게 아이템 정보 전송
        getAllStockUseCase.notifyStockUpdate(updatedItem);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleStockBulkUpdated(StockBulkUpdateEvent stockUpdateEvent) {

        // 변경된 모든 아이템에 대한 StockSummaryResult 조회
        stockUpdateEvent.productNumberList().forEach(productNumber -> {
            StockSummaryResult updatedResult = getAllStockUseCase.getStockByProductNumber(productNumber);
            getAllStockUseCase.notifyStockUpdate(updatedResult);
        });
    }
}
