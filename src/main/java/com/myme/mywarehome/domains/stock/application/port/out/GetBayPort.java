package com.myme.mywarehome.domains.stock.application.port.out;

import com.myme.mywarehome.domains.stock.application.domain.Bay;
import com.myme.mywarehome.domains.stock.application.port.in.result.BayWithStockBinResult;
import com.myme.mywarehome.domains.stock.application.port.in.result.BinInfoResult;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface GetBayPort {
    Page<BayWithStockBinResult> getAllBayList(Pageable pageable);
    List<BayWithStockBinResult> getAllBayByProductNumber(String productNumber);
    List<BinInfoResult> getBayByBayNumber(String bayNumber);
    Flux<ServerSentEvent<Object>> subscribeBayFluctuation();
    void emitBayUpdate(BayWithStockBinResult bayWithStockBinResult);
}
