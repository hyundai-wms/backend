package com.myme.mywarehome.domains.stock.application.service;

import com.myme.mywarehome.domains.receipt.application.domain.Receipt;
import com.myme.mywarehome.domains.stock.application.domain.Bin;
import com.myme.mywarehome.domains.stock.application.domain.Stock;
import com.myme.mywarehome.domains.stock.application.domain.StockEventType;
import com.myme.mywarehome.domains.stock.application.exception.NoAvailableBinException;
import com.myme.mywarehome.domains.stock.application.port.in.CreateStockUseCase;
import com.myme.mywarehome.domains.stock.application.port.out.CreateStockPort;
import com.myme.mywarehome.domains.stock.application.port.out.GetBinPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                .stockEventType(StockEventType.RECEIPT)
                .receipt(receipt)
                .build();

        // 2. 일단 저장하여 stockId를 생성하고, stockCode 생성
        Stock createdStock = createStockPort.create(stock);

        // todo : 적치 전략 알고리즘 설계
        // 현재는 bayNumber 오름차순으로 빈 bin을 가져옴.
        // 3. 적치 전략에 따른 bin을 가져옴
        Bin bin = getBinPort.findBinByProductNumber(receipt.getReceiptPlan().getProduct().getProductNumber())
                .orElseThrow(NoAvailableBinException::new);

        // 4. 연관관계 설정
        createdStock.assignBin(bin);

        return createdStock;
    }

    @Override
    @Transactional
    public void createStockBulk(List<Receipt> receiptList) {
        // 1. Receipt들의 product number 목록 추출
        List<String> productNumbers = receiptList.stream()
                .map(receipt -> receipt.getReceiptPlan().getProduct().getProductNumber())
                .distinct()
                .toList();

        // 2. 필요한 bin들을 미리 조회 (N+1 문제 방지)
        Map<String, List<Bin>> availableBinsByProduct = getBinPort.findAvailableBinsByProductNumbers(productNumbers);

        // 3. Receipt별로 Stock 생성 및 Bin 할당
        List<Stock> stocksToCreate = new ArrayList<>();

        for (Receipt receipt : receiptList) {
            String productNumber = receipt.getReceiptPlan().getProduct().getProductNumber();
            List<Bin> availableBins = availableBinsByProduct.get(productNumber);

            if (availableBins == null || availableBins.isEmpty()) {
                throw new NoAvailableBinException();
            }

            // Stock 생성
            Stock stock = Stock.builder()
                    .stockEventType(StockEventType.RECEIPT)
                    .receipt(receipt)
                    .build();

            // Bin 할당 (첫 번째 빈 Bin 사용)
            Bin selectedBin = availableBins.remove(0);
            stock.assignBin(selectedBin);

            stocksToCreate.add(stock);
        }

        // 4. 벌크 저장
        createStockPort.createBulk(stocksToCreate);
    }
}
