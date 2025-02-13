package com.myme.mywarehome.domains.receipt.application.domain;

import com.myme.mywarehome.infrastructure.common.jpa.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "outbound_products")
public class OutboundProduct extends BaseTimeEntity {
    // 외부 납품업체의 물품을 식별하기 위한 임시 엔티티
    // 다른 테이블과 연관관계를 가지지 않음

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String outboundProductId;

    private Long receiptPlanId;

    @Builder
    private OutboundProduct(Long id, String outboundProductId, Long receiptPlanId) {
        this.id = id;
        this.outboundProductId = outboundProductId;
        this.receiptPlanId = receiptPlanId;
    }
}
