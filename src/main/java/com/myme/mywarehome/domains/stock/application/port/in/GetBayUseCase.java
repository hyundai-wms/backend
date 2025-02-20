package com.myme.mywarehome.domains.stock.application.port.in;

import com.myme.mywarehome.domains.stock.application.port.in.result.BayWithStockBinResult;
import com.myme.mywarehome.domains.stock.application.port.in.result.BinInfoResult;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.List;

public interface GetBayUseCase {
    List<BinInfoResult> getBayByBayNumber(String bayNumber);
    List<BayWithStockBinResult> getBayListByProductNumber(String productNumber);
    Flux<ServerSentEvent<Object>> subscribeBayFluctuation();
    void notifyBayUpdate(BayWithStockBinResult bayWithStockBinResult);
}
