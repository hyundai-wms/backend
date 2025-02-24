package com.myme.mywarehome.domains.stock.application.service;

import com.myme.mywarehome.domains.issue.application.domain.Issue;
import com.myme.mywarehome.domains.stock.application.domain.Bin;
import com.myme.mywarehome.domains.stock.application.domain.Stock;
import com.myme.mywarehome.domains.stock.application.domain.StockEventType;
import com.myme.mywarehome.domains.stock.application.exception.StockNotFoundException;
import com.myme.mywarehome.domains.stock.application.port.out.GetStockPort;
import com.myme.mywarehome.domains.stock.application.port.out.UpdateStockPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssueAssignWithStockServiceTest {

    @Mock
    private GetStockPort getStockPort;

    @Mock
    private UpdateStockPort updateStockPort;

    private IssueAssignWithStockService issueAssignWithStockService;
    private Stock stock;
    private Issue issue;
    private Bin bin;

    @BeforeEach
    void setUp() {
        issueAssignWithStockService = new IssueAssignWithStockService(getStockPort, updateStockPort);

        bin = Bin.builder()
                .binId(1L)
                .binLocation(1)
                .build();

        stock = Stock.builder()
                .stockId(1L)
                .stockEventType(StockEventType.RECEIPT)
                .bin(bin)
                .build();

        issue = Issue.builder()
                .issueId(1L)
                .build();
    }

    @Test
    @DisplayName("재고에 출고를 할당하고 빈을 해제할 수 있다")
    void assignIssue_WhenValidStockAndIssue_ThenSuccess() {
        // given
        Long stockId = 1L;
        when(getStockPort.findById(stockId)).thenReturn(Optional.of(stock));
        when(updateStockPort.update(any(Stock.class))).thenReturn(stock);

        // when
        Stock result = issueAssignWithStockService.assignIssue(issue, stockId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStockId()).isEqualTo(stockId);
        assertThat(result.getIssue()).isEqualTo(issue);
        assertThat(result.getBin()).isNull();

        verify(getStockPort, times(1)).findById(stockId);
        verify(updateStockPort, times(1)).update(any(Stock.class));
    }

    @Test
    @DisplayName("존재하지 않는 재고 ID로 출고 할당 시 예외가 발생해야 한다")
    void assignIssue_WhenStockNotFound_ThenThrowException() {
        // given
        Long nonExistentStockId = 999L;
        when(getStockPort.findById(nonExistentStockId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> issueAssignWithStockService.assignIssue(issue, nonExistentStockId))
                .isInstanceOf(StockNotFoundException.class);

        verify(getStockPort, times(1)).findById(nonExistentStockId);
        verify(updateStockPort, never()).update(any(Stock.class));
    }
}