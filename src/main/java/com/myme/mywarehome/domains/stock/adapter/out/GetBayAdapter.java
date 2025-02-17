package com.myme.mywarehome.domains.stock.adapter.out;

import com.myme.mywarehome.domains.stock.adapter.out.persistence.BayJpaRepository;
import com.myme.mywarehome.domains.stock.application.domain.Bay;
import com.myme.mywarehome.domains.stock.application.port.in.result.BayWithStockBinResult;
import com.myme.mywarehome.domains.stock.application.port.in.result.BinInfoResult;
import com.myme.mywarehome.domains.stock.application.port.out.GetBayPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetBayAdapter implements GetBayPort {
    public final BayJpaRepository bayJpaRepository;

    @Override
    public Page<BayWithStockBinResult> getAllBayList(Pageable pageable) {
        return bayJpaRepository.findAllBaysWithStockCount(pageable);
    }

    @Override
    public List<BayWithStockBinResult> getAllBayByProductNumber(String productNumber) {
        return bayJpaRepository.findAllBaysByProductNumberWithStockCount(productNumber);
    }

    @Override
    public List<BinInfoResult> getBayByBayNumber(String bayNumber) {
        return bayJpaRepository.findByBayNumber(bayNumber);
    }
}
