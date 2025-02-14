package com.myme.mywarehome.domains.receipt.adapter.out.persistence;

import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.port.in.command.GetAllReceiptPlanCommand;
import java.time.LocalDate;
import java.util.List;

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
            "   CASE WHEN (COALESCE(:companyName, '') != '' OR COALESCE(:productName, '') != '') " +
            "   THEN (" +
            "       (COALESCE(:companyName, '') != '' AND rp.product.company.companyName LIKE CONCAT('%', :companyName, '%')) " +
            "       OR (COALESCE(:productName, '') != '' AND rp.product.productName LIKE CONCAT('%', :productName, '%')) " +
            "   ) " +
            "   ELSE true END" +
            ") " +
            "AND (rp.product.company.companyCode LIKE CONCAT('%', COALESCE(:companyCode, ''), '%')) " +
            "AND (CAST(:receiptPlanStartDate AS LocalDate) IS NULL OR rp.receiptPlanDate >= :receiptPlanStartDate) " +
            "AND (CAST(:receiptPlanEndDate AS LocalDate) IS NULL OR rp.receiptPlanDate <= :receiptPlanEndDate) " +
            "AND (rp.product.productNumber LIKE CONCAT('%', COALESCE(:productNumber, ''), '%')) ")
    Page<ReceiptPlan> findByConditions(
            @Param("companyName") String companyName,
            @Param("productName") String productName,
            @Param("companyCode") String companyCode,
            @Param("receiptPlanStartDate") LocalDate receiptPlanStartDate,
            @Param("receiptPlanEndDate") LocalDate receiptPlanEndDate,
            @Param("productNumber") String productNumber,
            Pageable pageable);

    @Query("SELECT DISTINCT rp FROM ReceiptPlan rp JOIN FETCH rp.product WHERE rp.receiptPlanDate = :date")
    List<ReceiptPlan> findByReceiptPlanDate(@Param("date") LocalDate date);
}
