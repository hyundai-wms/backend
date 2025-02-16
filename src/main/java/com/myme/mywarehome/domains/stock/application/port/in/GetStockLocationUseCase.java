package com.myme.mywarehome.domains.stock.application.port.in;

import com.myme.mywarehome.domains.stock.application.port.in.result.BayWithStockBin;
import java.util.List;

public interface GetStockLocationUseCase {
    List<BayWithStockBin> getBayList(String productNumber);
}
