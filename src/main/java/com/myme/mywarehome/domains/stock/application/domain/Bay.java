package com.myme.mywarehome.domains.stock.application.domain;

import com.myme.mywarehome.domains.product.application.domain.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "bays")
public class Bay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bayId;

    @Column(unique = true)
    private String bayNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "bay")
    private List<Bin> binList;

    @Builder
    private Bay(Long bayId, String bayNumber, Product product, List<Bin> binList) {
        this.bayId = bayId;
        this.bayNumber = bayNumber;
        this.product = product;
        this.binList = binList;
    }
}
