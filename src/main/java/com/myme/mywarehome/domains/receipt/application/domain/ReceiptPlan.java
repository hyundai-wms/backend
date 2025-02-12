package com.myme.mywarehome.domains.receipt.application.domain;

import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.infrastructure.common.jpa.BaseTimeEntity;
import com.myme.mywarehome.infrastructure.util.helper.StringHelper.CodeGenerator;
import jakarta.persistence.Column;
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
@Table(name = "receipt_plans")
public class ReceiptPlan extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long receiptPlanId;

    @Column(unique = true)
    private String receiptPlanCode;

    private Integer receiptPlanItemCount;

    private LocalDate receiptPlanDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id")
    private Product product;

    @Builder
    private ReceiptPlan(Long receiptPlanId, String receiptPlanCode, Integer receiptPlanItemCount, LocalDate receiptPlanDate, Product product) {
        this.receiptPlanId = receiptPlanId;
        this.receiptPlanCode = receiptPlanCode;
        this.receiptPlanItemCount = receiptPlanItemCount;
        this.receiptPlanDate = receiptPlanDate;
        this.product = product;
    }

    // 새로운 코드 부여
    public void generateReceiptPlanCode() {
        if(this.receiptPlanId != null) {
            this.receiptPlanCode = CodeGenerator.generateReceiptPlanCode(this.receiptPlanId);
        }
    }

    // 연결된 Product 설정
    public void connectWithProduct(Product product) {
        this.product = product;
    }

}
