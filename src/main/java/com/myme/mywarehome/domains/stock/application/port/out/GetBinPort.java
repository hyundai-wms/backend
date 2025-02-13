package com.myme.mywarehome.domains.stock.application.port.out;

import com.myme.mywarehome.domains.stock.application.domain.Bin;
import java.util.Optional;

public interface GetBinPort {
    Optional<Bin> findBinByProductNumber(String productNumber);
}
