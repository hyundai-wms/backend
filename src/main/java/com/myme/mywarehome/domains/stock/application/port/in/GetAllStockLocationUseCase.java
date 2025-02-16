package com.myme.mywarehome.domains.stock.application.port.in;

import com.myme.mywarehome.domains.stock.application.port.in.result.BayWithStockBinResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetAllStockLocationUseCase {
    Page<BayWithStockBinResult> getAllBayList(Pageable pageable);
}
