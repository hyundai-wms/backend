package com.myme.mywarehome.domains.stock.application.service;

import com.myme.mywarehome.domains.receipt.application.domain.Receipt;
import com.myme.mywarehome.domains.stock.application.domain.Bin;
import com.myme.mywarehome.domains.stock.application.domain.Stock;
import com.myme.mywarehome.domains.stock.application.exception.NoAvailableBin;
import com.myme.mywarehome.domains.stock.application.port.in.CreateStockUseCase;
import com.myme.mywarehome.domains.stock.application.port.out.CreateStockPort;
import com.myme.mywarehome.domains.stock.application.port.out.GetBinPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateStockService implements CreateStockUseCase {
    private final GetBinPort getBinPort;
    private final CreateStockPort createStockPort;

    @Override
    @Transactional
    public Stock createStock(Receipt receipt) {
        // 1. 기본 stock 생성
        Stock stock = Stock.builder()
                .receipt(receipt)
                .build();

        // 2. 일단 저장하여 stockId를 생성하고, stockCode 생성
        Stock createdStock = createStockPort.create(stock);

        // todo : 적치 전략 알고리즘 설계
        // 현재는 bayNumber 오름차순으로 빈 bin을 가져옴.
        // 3. 적치 전략에 따른 bin을 가져옴
        Bin bin = getBinPort.findBinByProductNumber(receipt.getProduct().getProductNumber())
                .orElseThrow(NoAvailableBin::new);

        // 4. 연관관계 설정
        createdStock.assignBin(bin);

        return createdStock;
    }
}
