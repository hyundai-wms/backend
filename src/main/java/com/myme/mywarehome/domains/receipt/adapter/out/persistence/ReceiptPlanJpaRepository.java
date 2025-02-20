package com.myme.mywarehome.domains.receipt.adapter.out.persistence;

import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.port.in.command.GetAllReceiptPlanCommand;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.myme.mywarehome.domains.receipt.application.port.in.result.TodayReceiptResult;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptPlanJpaRepository extends JpaRepository<ReceiptPlan, Long> {

    @EntityGraph(attributePaths = {"product", "product.company"})
    @Query("SELECT rp FROM ReceiptPlan rp WHERE " +
            "(" +
            "   CASE WHEN (COALESCE(:companyName, '') != '' OR COALESCE(:productName, '') != '' OR COALESCE(:receiptPlanCode, '') != '') " +
            "   THEN (" +
            "       (COALESCE(:companyName, '') != '' AND rp.product.company.companyName LIKE CONCAT('%', :companyName, '%')) " +
            "       OR (COALESCE(:productName, '') != '' AND rp.product.productName LIKE CONCAT('%', :productName, '%')) " +
            "       OR (COALESCE(:receiptPlanCode, '') != '' AND rp.receiptPlanCode LIKE CONCAT('%', :receiptPlanCode, '%')) " +
            "   ) " +
            "   ELSE true END" +
            ") " +
            "AND (rp.product.company.companyCode LIKE CONCAT('%', COALESCE(:companyCode, ''), '%')) " +
            "AND (CAST(:receiptPlanStartDate AS LocalDate) IS NULL OR rp.receiptPlanDate >= :receiptPlanStartDate) " +
            "AND (CAST(:receiptPlanEndDate AS LocalDate) IS NULL OR rp.receiptPlanDate <= :receiptPlanEndDate) " +
            "AND (rp.product.productNumber LIKE CONCAT('%', COALESCE(:productNumber, ''), '%')) " +
            "ORDER BY " +
            "   CASE WHEN rp.receiptPlanDate >= :selectedDate " +
            "       THEN 0 ELSE 1 END, " +
            "   CASE WHEN rp.receiptPlanDate >= :selectedDate " +
            "       THEN rp.receiptPlanDate " +
            "       ELSE NULL END ASC, " +
            "   CASE WHEN rp.receiptPlanDate < :selectedDate " +
            "       THEN rp.receiptPlanDate " +
            "       ELSE NULL END DESC")
    Page<ReceiptPlan> findByConditions(
            @Param("companyName") String companyName,
            @Param("productName") String productName,
            @Param("receiptPlanCode") String receiptPlanCode,
            @Param("companyCode") String companyCode,
            @Param("receiptPlanStartDate") LocalDate receiptPlanStartDate,
            @Param("receiptPlanEndDate") LocalDate receiptPlanEndDate,
            @Param("productNumber") String productNumber,
            @Param("selectedDate") LocalDate selectedDate,
            Pageable pageable);

    @Query("SELECT DISTINCT rp FROM ReceiptPlan rp JOIN FETCH rp.product WHERE rp.receiptPlanDate = :date")
    List<ReceiptPlan> findByReceiptPlanDate(@Param("date") LocalDate date);

    @EntityGraph(attributePaths = {"product", "product.company"})
    @Query("""
        SELECT new com.myme.mywarehome.domains.receipt.application.port.in.result.TodayReceiptResult(
            rp.receiptPlanId,
            rp.receiptPlanCode,
            rp.receiptPlanDate,
            CAST(COALESCE((SELECT COUNT(DISTINCT r2.receiptId) FROM Receipt r2 WHERE r2.receiptPlan = rp), 0) + 
                 COALESCE((SELECT COUNT(DISTINCT rt2.returnId) FROM Return rt2 WHERE rt2.receiptPlan = rp), 0) AS long),
            CAST(rp.receiptPlanItemCount AS long),
            CASE
                WHEN COALESCE((SELECT COUNT(DISTINCT r2.receiptId) FROM Receipt r2 WHERE r2.receiptPlan = rp), 0) + 
                     COALESCE((SELECT COUNT(DISTINCT rt2.returnId) FROM Return rt2 WHERE rt2.receiptPlan = rp), 0) = 0 
                THEN 'NOT_STARTED'
                WHEN COALESCE((SELECT COUNT(DISTINCT r2.receiptId) FROM Receipt r2 WHERE r2.receiptPlan = rp), 0) + 
                     COALESCE((SELECT COUNT(DISTINCT rt2.returnId) FROM Return rt2 WHERE rt2.receiptPlan = rp), 0) < rp.receiptPlanItemCount 
                THEN 'PROCESSING'
                ELSE 'DONE'
            END,
            rp.product.productNumber,
            rp.product.productName,
            rp.product.company.companyId,
            rp.product.company.companyCode,
            rp.product.company.companyName,
            rp.createdAt,
            rp.updatedAt
        )
        FROM ReceiptPlan rp
        WHERE rp.receiptPlanDate = :today
        """)
    Page<TodayReceiptResult> findTodayReceipts(LocalDate today, Pageable pageable);

    @EntityGraph(attributePaths = {"product", "product.company"})
    @Query("""
    SELECT new com.myme.mywarehome.domains.receipt.application.port.in.result.TodayReceiptResult(
        rp.receiptPlanId,
        rp.receiptPlanCode,
        rp.receiptPlanDate,
        CAST(COALESCE((SELECT COUNT(DISTINCT r2.receiptId) FROM Receipt r2 WHERE r2.receiptPlan = rp), 0) + 
             COALESCE((SELECT COUNT(DISTINCT rt2.returnId) FROM Return rt2 WHERE rt2.receiptPlan = rp), 0) AS long),
        CAST(rp.receiptPlanItemCount AS long),
        CASE
            WHEN COALESCE((SELECT COUNT(DISTINCT r2.receiptId) FROM Receipt r2 WHERE r2.receiptPlan = rp), 0) + 
                 COALESCE((SELECT COUNT(DISTINCT rt2.returnId) FROM Return rt2 WHERE rt2.receiptPlan = rp), 0) = 0 
            THEN 'NOT_STARTED'
            WHEN COALESCE((SELECT COUNT(DISTINCT r2.receiptId) FROM Receipt r2 WHERE r2.receiptPlan = rp), 0) + 
                 COALESCE((SELECT COUNT(DISTINCT rt2.returnId) FROM Return rt2 WHERE rt2.receiptPlan = rp), 0) < rp.receiptPlanItemCount 
            THEN 'PROCESSING'
            ELSE 'DONE'
        END,
        rp.product.productNumber,
        rp.product.productName,
        rp.product.company.companyId,
        rp.product.company.companyCode,
        rp.product.company.companyName,
        rp.createdAt,
        rp.updatedAt
    )
    FROM ReceiptPlan rp
    WHERE rp.receiptPlanId = :receiptPlanId
    AND rp.receiptPlanDate = :today
    """)
    Optional<TodayReceiptResult> findTodayReceiptById(
            @Param("receiptPlanId") Long receiptPlanId,
            @Param("today") LocalDate today
    );

    @Query("SELECT COUNT(rp) FROM ReceiptPlan rp WHERE rp.receiptPlanDate = :selectedDate")
    Integer countByReceiptPlanDate(@Param("selectedDate") LocalDate selectedDate);


}
