package com.myme.mywarehome.domains.stock.application.port.in;

import com.myme.mywarehome.domains.stock.application.port.in.result.BayWithStockBinResult;
import java.util.List;

public interface GetStockLocationUseCase {
    List<BayWithStockBinResult> getBayList(String productNumber);
}
