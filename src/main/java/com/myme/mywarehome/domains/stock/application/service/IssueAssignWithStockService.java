package com.myme.mywarehome.domains.stock.application.service;

import com.myme.mywarehome.domains.issue.application.domain.Issue;
import com.myme.mywarehome.domains.stock.application.domain.Stock;
import com.myme.mywarehome.domains.stock.application.exception.StockNotFoundException;
import com.myme.mywarehome.domains.stock.application.port.in.IssueAssignWithStockUseCase;
import com.myme.mywarehome.domains.stock.application.port.out.GetStockPort;
import com.myme.mywarehome.domains.stock.application.port.out.UpdateStockPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IssueAssignWithStockService implements IssueAssignWithStockUseCase {
    public final GetStockPort getStockPort;
    public final UpdateStockPort updateStockPort;

    @Override
    @Transactional
    public Stock assignIssue(Issue issue, Long stockId) {
        // 1. Stock 조회
        Stock stock = getStockPort.findById(stockId)
                .orElseThrow(StockNotFoundException::new);

        // 2. Issue 할당
        stock.assignIssue(issue);

        // 3. Stock에 연결된 Bin 제거
        stock.releaseBin();

        // 4. 저장 및 반환
        return updateStockPort.update(stock);
    }
}
