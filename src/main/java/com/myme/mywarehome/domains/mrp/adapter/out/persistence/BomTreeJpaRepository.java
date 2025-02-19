package com.myme.mywarehome.domains.mrp.adapter.out.persistence;

import com.myme.mywarehome.domains.mrp.application.domain.BomTree;
import com.myme.mywarehome.domains.product.application.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface BomTreeJpaRepository extends JpaRepository<BomTree, Long> {
    @Query("SELECT bt FROM BomTree bt " +
            "WHERE bt.parentProduct.productNumber = :rootProductNumber " +
            "OR bt.parentProduct.productNumber IN " +
            "(SELECT b.childProduct.productNumber FROM BomTree b WHERE b.parentProduct.productNumber = :rootProductNumber)")
    List<BomTree> findAllByRootProduct(@Param("rootProductNumber") String rootProductNumber);

    @Query("SELECT b FROM BomTree b WHERE b.parentProduct.productNumber = :parentProductNumber")
    List<BomTree> findAllByParentProductNumber(@Param("parentProductNumber") String parentProductNumber);
}
