package com.myme.mywarehome.domains.product.adapter.out.persistence;

import com.myme.mywarehome.domains.mrp.application.port.in.result.ProductStockCount;
import com.myme.mywarehome.domains.product.application.domain.Product;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product, Long> {
    //Page<Product> findAll(Pageable pageable);

    Optional<Product> findByProductNumber(String productNumber);

    @Query("SELECT p FROM Product p WHERE p.productNumber IN :productNumberList")
    List<Product> findAllByProductNumberIn(@Param("productNumberList") Set<String> productNumberList);

    List<Product> findByApplicableEngine(String applicableEngine);

    @Query("""
        SELECT new com.myme.mywarehome.domains.mrp.application.port.in.result.ProductStockCount(
               p.productId,
               p.productNumber,
               p.productName,
               COALESCE(bt.childCompositionRatio, 1),
               p.leadTime,
               COUNT(s)
        )
        FROM Product p
        LEFT JOIN BomTree bt ON bt.childProduct = p
        LEFT JOIN ReceiptPlan rp ON rp.product = p
        LEFT JOIN Receipt r ON r.receiptPlan = rp
        LEFT JOIN Stock s ON s.receipt = r
        WHERE s.issue IS NULL
        GROUP BY p.productId, p.productNumber, p.productName, bt.childCompositionRatio, p.eachCount, p.leadTime
    """)
    List<ProductStockCount> findAllProductsWithAvailableStockCount();
}
