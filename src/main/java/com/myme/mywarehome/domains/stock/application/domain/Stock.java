package com.myme.mywarehome.domains.stock.application.domain;

import com.myme.mywarehome.domains.issue.application.domain.Issue;
import com.myme.mywarehome.domains.receipt.application.domain.Receipt;
import com.myme.mywarehome.infrastructure.common.jpa.BaseTimeEntity;
import com.myme.mywarehome.infrastructure.util.helper.StringHelper.CodeGenerator;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "stocks")
public class Stock extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stockId;

    @Column(unique = true)
    private String stockCode;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id")
    private Receipt receipt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id")
    private Issue issue;

    @Enumerated(EnumType.STRING)
    private StockEventType lastEventType;

    @OneToOne(mappedBy = "stock")
    private Bin bin;

    @Version
    private Long version;

    @Builder
    private Stock(Long stockId, String stockCode, Receipt receipt, StockEventType stockEventType, Bin bin, Issue issue) {
        this.stockId = stockId;
        this.stockCode = stockCode;
        this.receipt = receipt;
        this.lastEventType = stockEventType;
        this.bin = bin;
        this.issue = issue;
    }

    // 새로운 코드 부여
    @PostPersist
    private void generateStockCode() {
        this.stockCode = CodeGenerator.generateStockCode(stockId);
    }

    // 연결된 Bin 설정
    public void assignBin(Bin bin) {
        // 기존 관계 제거
        if (this.bin != null) {
            this.bin.connectWithStock(null);
        }

        this.bin = bin;
        if (bin != null && bin.getStock() != this) {
            bin.connectWithStock(this);
        }
    }

    // 연결된 Issue 설정 + 양방향 연결
    public void assignIssue(Issue issue) {
        this.issue = issue;
        this.lastEventType = StockEventType.ISSUE;
        if (issue != null && issue.getStock() != this) {
            issue.connectWithStock(this);
        }
    }
}
