package com.myme.mywarehome.domains.stock.adapter.out;

import com.myme.mywarehome.domains.stock.adapter.out.persistence.BayJpaRepository;
import com.myme.mywarehome.domains.stock.application.port.in.result.BayWithStockBin;
import com.myme.mywarehome.domains.stock.application.port.out.GetBayPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetBayAdapter implements GetBayPort {
    public final BayJpaRepository bayJpaRepository;

    @Override
    public Page<BayWithStockBin> getAllBayList(Pageable pageable) {
        return bayJpaRepository.findAllBaysWithStockCount(pageable);
    }
}
