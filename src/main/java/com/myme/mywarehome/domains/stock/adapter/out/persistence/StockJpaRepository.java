package com.myme.mywarehome.domains.stock.adapter.out.persistence;

import com.myme.mywarehome.domains.stock.application.domain.Stock;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockJpaRepository extends JpaRepository<Stock, Long> {

    @Query("SELECT s FROM Stock s " +
            "JOIN FETCH s.bin b " +
            "JOIN FETCH b.bay ba " +
            "JOIN FETCH ba.product p " +
            "JOIN FETCH s.receipt r " +
            "JOIN FETCH r.receiptPlan rp " +
            "WHERE p.productNumber = :productNumber " +
            "AND s.receipt IS NOT NULL " +
            "AND s.issue IS NULL " +
            "ORDER BY " +
            "   CASE WHEN rp.receiptPlanDate >= :selectedDate THEN 0 ELSE 1 END, " +
            "   CASE WHEN rp.receiptPlanDate >= :selectedDate " +
            "       THEN rp.receiptPlanDate " +
            "       ELSE NULL END ASC, " +
            "   CASE WHEN rp.receiptPlanDate < :selectedDate " +
            "       THEN rp.receiptPlanDate " +
            "       ELSE NULL END DESC")
    Page<Stock> findByProductNumber(
            @Param("productNumber") String productNumber,
            @Param("selectedDate") LocalDate selectedDate,
            Pageable pageable
    );
}
