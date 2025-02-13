package com.myme.mywarehome.domains.receipt.application.domain;

import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.infrastructure.common.jpa.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "returns")
public class Return extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long returnId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_plan_id")
    private ReceiptPlan receiptPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id")
    private Product product;

    @Builder
    private Return(Long returnId, ReceiptPlan receiptPlan, Product product) {
        this.returnId = returnId;
        this.receiptPlan = receiptPlan;
        this.product = product;
    }

    // 연결된 ReceiptPlan 설정
    public void connectWithReceiptPlan(ReceiptPlan receiptPlan) {
        this.receiptPlan = receiptPlan;
    }

    // 연결된 Product 설정
    public void connectWithProduct(Product product) {
        this.product = product;
    }
}
