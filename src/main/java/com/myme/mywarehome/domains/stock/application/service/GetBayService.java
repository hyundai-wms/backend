package com.myme.mywarehome.domains.stock.application.service;

import com.myme.mywarehome.domains.stock.application.port.in.GetBayUseCase;
import com.myme.mywarehome.domains.stock.application.port.in.result.BayWithStockBinResult;
import com.myme.mywarehome.domains.stock.application.port.in.result.BinInfoResult;
import com.myme.mywarehome.domains.stock.application.port.out.GetBayPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class GetBayService implements GetBayUseCase {
    private final GetBayPort getBayPort;

    @Override
    public List<BinInfoResult> getBayByBayNumber(String bayNumber) {
        return getBayPort.getBayByBayNumber(bayNumber);
    }

    @Override
    public List<BayWithStockBinResult> getBayListByProductNumber(String productNumber) {
        return getBayPort.getAllBayByProductNumber(productNumber);
    }

    @Override
    public Flux<ServerSentEvent<Object>> subscribeBayFluctuation() {
        return getBayPort.subscribeBayFluctuation();
    }

    @Override
    public void notifyBayUpdate(BayWithStockBinResult bayWithStockBinResult) {
        getBayPort.emitBayUpdate(bayWithStockBinResult);
    }


}
