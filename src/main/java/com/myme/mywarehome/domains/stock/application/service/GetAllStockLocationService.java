package com.myme.mywarehome.domains.stock.application.service;

import com.myme.mywarehome.domains.stock.application.port.in.GetAllStockLocationUseCase;
import com.myme.mywarehome.domains.stock.application.port.in.result.BayWithStockBinResult;
import com.myme.mywarehome.domains.stock.application.port.out.GetBayPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class GetAllStockLocationService implements GetAllStockLocationUseCase {
    public final GetBayPort getBayPort;

    @Override
    public Page<BayWithStockBinResult> getAllBayList(Pageable pageable) {
        return getBayPort.getAllBayList(pageable);
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
