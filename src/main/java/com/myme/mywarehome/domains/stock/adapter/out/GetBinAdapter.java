package com.myme.mywarehome.domains.stock.adapter.out;

import com.myme.mywarehome.domains.stock.adapter.out.persistence.BinJpaRepository;
import com.myme.mywarehome.domains.stock.application.domain.Bin;
import com.myme.mywarehome.domains.stock.application.port.out.GetBinPort;
import java.util.Optional;
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
}
