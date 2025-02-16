package com.myme.mywarehome.domains.stock.application.service;

import com.myme.mywarehome.domains.stock.application.port.in.GetStockLocationUseCase;
import com.myme.mywarehome.domains.stock.application.port.in.result.BayWithStockBin;
import com.myme.mywarehome.domains.stock.application.port.out.GetBayPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetStockLocationService implements GetStockLocationUseCase {
    public final GetBayPort getBayPort;

    @Override
    public List<BayWithStockBin> getBayList(String productNumber) {
        return getBayPort.getAllBayByProductNumber(productNumber);
    }
}
