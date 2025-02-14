package com.myme.mywarehome.domains.stock.adapter.out.persistence;

import com.myme.mywarehome.domains.stock.application.domain.Bin;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BinJpaRepository extends JpaRepository<Bin, Long> {
    @Query("""
            SELECT b
            FROM Bin b
            JOIN FETCH b.bay bay
            WHERE bay.product.productNumber = :productNumber
            AND b.stock IS NULL
            ORDER BY bay.bayNumber ASC, b.binLocation ASC
            LIMIT 1
            """)
    Optional<Bin> findFirstEmptyBinByProductNumber(@Param("productNumber") String productNumber);

    @Query("""
    SELECT b
    FROM Bin b
    JOIN FETCH b.bay bay
    WHERE bay.product.productNumber IN :productNumbers
    AND b.stock IS NULL
    ORDER BY bay.bayNumber ASC, b.binLocation ASC
    """)
    List<Bin> findEmptyBinsByProductNumbers(@Param("productNumbers") List<String> productNumbers);
}
