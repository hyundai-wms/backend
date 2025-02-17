package com.myme.mywarehome.domains.mrp.adapter.out.persistence;

import com.myme.mywarehome.domains.mrp.application.domain.BomTree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface BomTreeJpaRepository extends JpaRepository<BomTree, Long> {
    @Query("SELECT bt FROM BomTree bt " +
            "WHERE bt.parentProduct.productNumber = :rootProductNumber " +
            "OR bt.parentProduct.productNumber IN " +
            "(SELECT b.childProduct.productNumber FROM BomTree b WHERE b.parentProduct.productNumber = :rootProductNumber)")
    List<BomTree> findAllByRootProduct(@Param("rootProductNumber") String rootProductNumber);
}
