package com.myme.mywarehome.domains.stock.application.port.out;

import com.myme.mywarehome.domains.stock.application.domain.Bin;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GetBinPort {
    Optional<Bin> findBinByProductNumber(String productNumber);
    Map<String, List<Bin>> findAvailableBinsByProductNumbers(List<String> productNumberList);
}
