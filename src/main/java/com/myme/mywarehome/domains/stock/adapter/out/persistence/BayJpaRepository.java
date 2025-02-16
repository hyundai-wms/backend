package com.myme.mywarehome.domains.stock.adapter.out.persistence;

import com.myme.mywarehome.domains.stock.application.domain.Bay;
import com.myme.mywarehome.domains.stock.application.port.in.result.BayWithStockBinResult;
import com.myme.mywarehome.domains.stock.application.port.in.result.BinInfoResult;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BayJpaRepository extends JpaRepository<Bay, Long> {
    @Query("""
        SELECT new com.myme.mywarehome.domains.stock.application.port.in.result.BayWithStockBinResult(
            b.bayId,
            b.bayNumber,
            p.productNumber,
            SUM(CASE WHEN s.stockId IS NULL THEN 0 ELSE 1 END)
        )
        FROM Bay b
        LEFT JOIN b.product p
        LEFT JOIN b.binList bin
        LEFT JOIN bin.stock s
        GROUP BY b.bayId, b.bayNumber, p.productNumber
        ORDER BY b.bayId
        """)
    Page<BayWithStockBinResult> findAllBaysWithStockCount(Pageable pageable);

    @Query("""
        SELECT new com.myme.mywarehome.domains.stock.application.port.in.result.BayWithStockBinResult(
            b.bayId,
            b.bayNumber,
            p.productNumber,
            SUM(CASE WHEN s.stockId IS NULL THEN 0 ELSE 1 END)
        )
        FROM Bay b
        LEFT JOIN b.product p
        LEFT JOIN b.binList bin
        LEFT JOIN bin.stock s
        WHERE p.productNumber = :productNumber
        GROUP BY b.bayId, b.bayNumber, p.productNumber
        ORDER BY b.bayId
        """)
    List<BayWithStockBinResult> findAllBaysByProductNumberWithStockCount(@Param("productNumber") String productNumber);

    @Query("SELECT new com.myme.mywarehome.domains.stock.application.port.in.result.BinInfoResult(" +
            "b.bayNumber, p.productNumber, p.productName, bin.binLocation, " +
            "s.stockId, s.stockCode, r.receiptId, r.receiptCode, r.receiptDate, " +
            "c.companyId, c.companyCode, c.companyName) " +
            "FROM Bay b " +
            "JOIN b.product p " +
            "JOIN p.company c " +
            "JOIN b.binList bin " +
            "JOIN bin.stock s " +
            "JOIN s.receipt r " +
            "WHERE b.bayNumber = :bayNumber")
    List<BinInfoResult> findByBayNumber(@Param("bayNumber") String bayNumber);
}
