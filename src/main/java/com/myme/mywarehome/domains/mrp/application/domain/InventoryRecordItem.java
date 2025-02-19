package com.myme.mywarehome.domains.mrp.application.domain;

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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "inventory_record_items")
public class InventoryRecordItem extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryRecordItemId;

    @Column(unique = true)
    private String inventoryRecordItemCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_record_id")
    private InventoryRecord inventoryRecord;

    private Long stockCount;

    private Integer compositionRatio;

    private Integer leadTime;

    @Builder
    private InventoryRecordItem(Long inventoryRecordItemId, String inventoryRecordItemCode, Product product, InventoryRecord inventoryRecord, Long stockCount, Integer compositionRatio, Integer leadTime) {
        this.inventoryRecordItemId = inventoryRecordItemId;
        this.inventoryRecordItemCode = inventoryRecordItemCode;
        this.product = product;
        this.inventoryRecord = inventoryRecord;
        this.stockCount = stockCount;
        this.compositionRatio = compositionRatio;
        this.leadTime = leadTime;
    }

    // 새로운 코드 부여
    @PostPersist
    private void generateInventoryRecordItemCode() {
        this.inventoryRecordItemCode = CodeGenerator.generateInventoryRecordItemCode(inventoryRecordItemId);
    }


}
