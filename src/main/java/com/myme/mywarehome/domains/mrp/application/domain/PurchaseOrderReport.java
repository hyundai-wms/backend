package com.myme.mywarehome.domains.mrp.application.domain;

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
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "purchase_order_reports")
public class PurchaseOrderReport extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseOrderReportId;

    private LocalDate purchaseOrderDate;

    private LocalDate receiptPlanDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;

    @Builder
    public PurchaseOrderReport(Long purchaseOrderReportId, LocalDate purchaseOrderDate, LocalDate receiptPlanDate, Product product, Integer quantity) {
        this.purchaseOrderReportId = purchaseOrderReportId;
        this.purchaseOrderDate = purchaseOrderDate;
        this.receiptPlanDate = receiptPlanDate;
        this.product = product;
        this.quantity = quantity;
    }
}
