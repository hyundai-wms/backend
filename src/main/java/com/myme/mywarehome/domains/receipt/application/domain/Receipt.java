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
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "receipts")
public class Receipt extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long receiptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_plan_id")
    private ReceiptPlan receiptPlan;

    @Column(unique = true)
    private String receiptCode;

    private LocalDate receiptDate;

    @Builder
    private Receipt(Long receiptId, ReceiptPlan receiptPlan, Product product, String receiptCode, LocalDate receiptDate) {
        this.receiptId = receiptId;
        this.receiptPlan = receiptPlan;
        this.receiptCode = receiptCode;
        this.receiptDate = receiptDate;
    }

    // 새로운 코드 부여
    @PostPersist
    public void generateReceiptCode() {
        this.receiptCode = CodeGenerator.generateReceiptCode(this.receiptId);
    }

    // 연결된 ReceiptPlan 설정
    public void connectWithReceiptPlan(ReceiptPlan receiptPlan) {
        this.receiptPlan = receiptPlan;
    }
}
