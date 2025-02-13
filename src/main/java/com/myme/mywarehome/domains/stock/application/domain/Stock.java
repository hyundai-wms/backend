package com.myme.mywarehome.domains.stock.application.domain;

import com.myme.mywarehome.domains.receipt.application.domain.Receipt;
import com.myme.mywarehome.infrastructure.common.jpa.BaseTimeEntity;
import com.myme.mywarehome.infrastructure.util.helper.StringHelper.CodeGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
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
    @JoinColumn(name = "receipt_id", unique = true)
    private Receipt receipt;

    // todo : issue 쪽도 추가해야 함

    @OneToOne(mappedBy = "stock")
    private Bin bin;

    @Version
    private Long version;

    // 새로운 코드 부여
    public void generateStockCode() {
        if(this.stockId != null){
            this.stockCode = CodeGenerator.generateStockCode(stockId);
        }
    }

    // 연결된 Receipt 설정
    public void connectWithReceipt(Receipt receipt) {
        this.receipt = receipt;
    }
}
