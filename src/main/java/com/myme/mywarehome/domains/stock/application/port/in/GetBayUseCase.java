package com.myme.mywarehome.domains.stock.application.port.in;

import com.myme.mywarehome.domains.stock.application.port.in.result.BinInfoResult;
import java.util.List;

public interface GetBayUseCase {
    List<BinInfoResult> getBayByBayNumber(String bayNumber);
}
