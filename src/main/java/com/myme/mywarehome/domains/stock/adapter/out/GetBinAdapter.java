package com.myme.mywarehome.domains.stock.adapter.out;

import com.myme.mywarehome.domains.stock.adapter.out.persistence.BinJpaRepository;
import com.myme.mywarehome.domains.stock.application.domain.Bin;
import com.myme.mywarehome.domains.stock.application.port.out.GetBinPort;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetBinAdapter implements GetBinPort {
    private final BinJpaRepository binJpaRepository;

    @Override
    public Optional<Bin> findBinByProductNumber(String productNumber) {
        return binJpaRepository.findFirstEmptyBinByProductNumber(productNumber);
    }

    @Override
    public Map<String, List<Bin>> findAvailableBinsByProductNumbers(List<String> productNumberList) {
        List<Bin> emptyBins = binJpaRepository.findEmptyBinsByProductNumbers(productNumberList);

        return emptyBins.stream()
                .collect(Collectors.groupingBy(
                        bin -> bin.getBay().getProduct().getProductNumber(),
                        Collectors.toList()
                ));
    }
}
