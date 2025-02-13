package com.myme.mywarehome.domains.issue.adapter.out.persistence;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import java.time.LocalDate;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IssuePlanJpaRepository extends JpaRepository<IssuePlan, Long> {

    @EntityGraph(attributePaths = {"product", "product.company"})
    @Query("SELECT ip FROM IssuePlan ip WHERE " +
            "(" +
            "   CASE WHEN (COALESCE(:companyName, '') != '' OR COALESCE(:productName, '') != '') " +
            "   THEN (" +
            "       (COALESCE(:companyName, '') != '' AND ip.product.company.companyName LIKE CONCAT('%', :companyName, '%')) " +
            "       OR (COALESCE(:productName, '') != '' AND ip.product.productName LIKE CONCAT('%', :productName, '%')) " +
            "   ) " +
            "   ELSE true END" +
            ") " +
            "AND (ip.product.company.companyCode LIKE CONCAT('%', COALESCE(:companyCode, ''), '%')) " +
            "AND (CAST(:issuePlanStartDate AS LocalDate) IS NULL OR ip.issuePlanDate >= :issuePlanStartDate) " +
            "AND (CAST(:issuePlanEndDate AS LocalDate) IS NULL OR ip.issuePlanDate <= :issuePlanEndDate) " +
            "AND (ip.product.productNumber LIKE CONCAT('%', COALESCE(:productNumber, ''), '%')) " +
            "And (ip.issuePlanCode LIKE CONCAT('%', COALESCE(:issuePlanCode, ''), '%'))"

    )
    Page<IssuePlan> findByConditions(
            @Param("companyName") String companyName,
            @Param("productName") String productName,
            @Param("companyCode") String companyCode,
            @Param("issuePlanStartDate") LocalDate issuePlanStartDate,
            @Param("issuePlanEndDate") LocalDate issuePlanEndDate,
            @Param("productNumber") String productNumber,
            @Param("issuePlanCode") String issuePlanCode,
            Pageable pageable);
}
