package com.myme.mywarehome.domains.product.application.domain;

import com.myme.mywarehome.domains.company.application.domain.Company;
import com.myme.mywarehome.infrastructure.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "products")
public class Product extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    private String productNumber;
    private String productName;
    private Integer eachCount;
    private Integer safeItemCount;
    private Integer leadTime;
    private String applicableEngine;


    @Transient  // DB 컬럼으로는 만들지 않음
    private Integer page;

    @Transient
    private Integer size;

    @Transient
    private String sort;


    @Builder
    public Product(Long productId, String productNumber, String productName, Integer eachCount, Integer safeItemCount, Integer leadTime, String applicableEngine ,Integer page, Integer size, String sort, Company company) {
        this.productId = productId;
        this.productNumber = productNumber;
        this.productName = productName;
        this.eachCount = eachCount;
        this.safeItemCount = safeItemCount;
        this.leadTime = leadTime;
        this.applicableEngine = applicableEngine;
        this.company = company;
        this.page = page;
        this.size = size;
        this.sort = sort;
    }
}
