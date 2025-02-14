package com.myme.mywarehome.domains.receipt.adapter.out.persistence;

import com.myme.mywarehome.domains.receipt.application.domain.Receipt;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ReceiptJpaRepository extends JpaRepository<Receipt, Long> {

    @EntityGraph(attributePaths = {"receiptPlan", "receiptPlan.product", "receiptPlan.product.company"})
    @Query("SELECT r FROM Receipt r WHERE " +
            "(" +
            "   CASE WHEN (COALESCE(:companyName, '') != '' OR COALESCE(:productName, '') != '') " +
            "   THEN (" +
            "       (COALESCE(:companyName, '') != '' AND r.receiptPlan.product.company.companyName LIKE CONCAT('%', :companyName, '%')) " +
            "       OR (COALESCE(:productName, '') != '' AND r.receiptPlan.product.productName LIKE CONCAT('%', :productName, '%')) " +
            "   ) " +
            "   ELSE true END" +
            ") " +
            "AND (r.receiptPlan.product.company.companyCode LIKE CONCAT('%', COALESCE(:companyCode, ''), '%')) " +
            "AND (r.receiptPlan.receiptPlanCode LIKE CONCAT('%', COALESCE(:receiptPlanCode, ''), '%')) " +
            "AND (CAST(:receiptPlanStartDate AS LocalDate) IS NULL OR r.receiptPlan.receiptPlanDate >= :receiptPlanStartDate) " +
            "AND (CAST(:receiptPlanEndDate AS LocalDate) IS NULL OR r.receiptPlan.receiptPlanDate <= :receiptPlanEndDate) " +
            "AND (r.receiptPlan.product.productNumber LIKE CONCAT('%', COALESCE(:productNumber, ''), '%')) "+
            "AND (r.receiptCode LIKE CONCAT('%', COALESCE(:receiptCode, ''), '%')) " +
            "AND (CAST(:receiptStartDate AS LocalDate) IS NULL OR r.receiptDate >= :receiptStartDate) " +
            "AND (CAST(:receiptEndDate AS LocalDate) IS NULL OR r.receiptDate <= :receiptEndDate) ")
    Page<Receipt> findByConditions(
            @Param("companyCode") String companyCode,
            @Param("companyName") String companyName,
            @Param("receiptPlanCode") String receiptPlanCode,
            @Param("receiptPlanStartDate") LocalDate receiptPlanStartDate,
            @Param("receiptPlanEndDate") LocalDate receiptPlanEndDate,
            @Param("productNumber") String productNumber,
            @Param("productName") String productName,
            @Param("receiptCode") String receiptCode,
            @Param("receiptStartDate") LocalDate receiptStartDate,
            @Param("receiptEndDate") LocalDate receiptEndDate,
            Pageable pageable
    );
}
