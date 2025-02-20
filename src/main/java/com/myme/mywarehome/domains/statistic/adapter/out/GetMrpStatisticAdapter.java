package com.myme.mywarehome.domains.statistic.adapter.out;

import com.myme.mywarehome.domains.receipt.adapter.out.persistence.ReceiptJpaRepository;
import com.myme.mywarehome.domains.receipt.adapter.out.persistence.ReturnJpaRepository;
import com.myme.mywarehome.domains.statistic.adapter.in.web.response.GetMrpStatisticResponse.CompanyProductCount;
import com.myme.mywarehome.domains.statistic.adapter.in.web.response.GetMrpStatisticResponse.CompanyReturnCount;
import com.myme.mywarehome.domains.statistic.application.port.out.GetMrpStatisticPort;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetMrpStatisticAdapter implements GetMrpStatisticPort {
    private final ReturnJpaRepository returnJpaRepository;
    private final ReceiptJpaRepository receiptJpaRepository;

    @Override
    public List<CompanyReturnCount> getTop5ReturnCounts() {
        return returnJpaRepository.findTop5CompanyReturnCounts().stream()
                .map(result -> new CompanyReturnCount(
                        (String) result[0],
                        ((Number) result[1]).intValue()
                ))
                .collect(Collectors.toList());
    }
    @Override
    public Integer getTotalReturnCount() {
        return Math.toIntExact(returnJpaRepository.count());
    }

    @Override
    public List<CompanyProductCount> getTop5ProductCounts() {
        return receiptJpaRepository.findTop5CompanyReceiptCounts().stream()
                .map(result -> new CompanyProductCount(
                        (String) result[0],
                        ((Number) result[1]).intValue()
                ))
                .collect(Collectors.toList());
    }
}
