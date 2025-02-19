package com.myme.mywarehome.domains.mrp.application.domain;

import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.infrastructure.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "bom_trees")
public class BomTree extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="bom_tree_id")
    private Long bomTreeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_product_id")
    private Product parentProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_product_id")
    private Product childProduct;

    private Integer childCompositionRatio;

    @Builder
    public BomTree(Product parentProduct, Product childProduct, Integer childCompositionRatio) {
        this.parentProduct = parentProduct;
        this.childProduct = childProduct;
        this.childCompositionRatio = childCompositionRatio;
    }


}
