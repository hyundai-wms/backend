package com.myme.mywarehome.domains.stock.application.port.out;

import com.myme.mywarehome.domains.stock.application.port.in.result.BayWithStockBin;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetBayPort {
    Page<BayWithStockBin> getAllBayList(Pageable pageable);
    List<BayWithStockBin> getAllBayByProductNumber(String productNumber);
}
