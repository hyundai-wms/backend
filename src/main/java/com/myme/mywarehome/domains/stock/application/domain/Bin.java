package com.myme.mywarehome.domains.stock.application.domain;

import com.myme.mywarehome.infrastructure.common.jpa.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "bins")
public class Bin extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long binId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", unique = true)
    private Stock stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bay_id")
    private Bay bay;

    private Integer binLocation;

    @Version
    private Long version;

    @Builder
    private Bin(Long binId, Stock stock, Bay bay, Integer binLocation) {
        this.binId = binId;
        this.stock = stock;
        this.bay = bay;
        this.binLocation = binLocation;
    }

    // 연결된 Stock 설정
    public void connectWithStock(Stock stock) {
        this.stock = stock;
    }
}
