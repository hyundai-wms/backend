package com.myme.mywarehome.domains.stock.application.port.in;

import com.myme.mywarehome.domains.stock.application.port.in.result.BayWithStockBinResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface GetAllStockLocationUseCase {
    Page<BayWithStockBinResult> getAllBayList(Pageable pageable);
    Flux<ServerSentEvent<Object>> subscribeBayFluctuation();
    void notifyBayUpdate(BayWithStockBinResult bayWithStockBinResult);
}
