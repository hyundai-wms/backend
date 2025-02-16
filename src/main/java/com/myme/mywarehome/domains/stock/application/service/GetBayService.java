package com.myme.mywarehome.domains.stock.application.service;

import com.myme.mywarehome.domains.stock.application.port.in.GetBayUseCase;
import com.myme.mywarehome.domains.stock.application.port.in.result.BinInfoResult;
import com.myme.mywarehome.domains.stock.application.port.out.GetBayPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetBayService implements GetBayUseCase {
    private final GetBayPort getBayPort;

    @Override
    public List<BinInfoResult> getBayByBayNumber(String bayNumber) {
        return getBayPort.getBayByBayNumber(bayNumber);
    }
}
