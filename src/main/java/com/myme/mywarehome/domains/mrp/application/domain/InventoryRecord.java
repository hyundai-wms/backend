package com.myme.mywarehome.domains.mrp.application.domain;

import com.myme.mywarehome.infrastructure.common.jpa.BaseTimeEntity;
import com.myme.mywarehome.infrastructure.util.helper.StringHelper.CodeGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "inventory_records")
public class InventoryRecord extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryRecordId;

    @Column(unique = true)
    private String inventoryRecordCode;

    private LocalDateTime stockStatusAt;

    @OneToMany(mappedBy = "inventoryRecord")
    private List<InventoryRecordItem> inventoryRecordItemList;

    @Builder
    public InventoryRecord(Long inventoryRecordId, String inventoryRecordCode,
            LocalDateTime stockStatusAt, List<InventoryRecordItem> inventoryRecordItemList) {
        this.inventoryRecordId = inventoryRecordId;
        this.inventoryRecordCode = inventoryRecordCode;
        this.stockStatusAt = stockStatusAt;
        this.inventoryRecordItemList = inventoryRecordItemList;
    }

    // 새로운 코드 부여
    @PostPersist
    private void generateStockCode() {
        this.inventoryRecordCode = CodeGenerator.generateInventoryRecordCode(inventoryRecordId);
    }
}
