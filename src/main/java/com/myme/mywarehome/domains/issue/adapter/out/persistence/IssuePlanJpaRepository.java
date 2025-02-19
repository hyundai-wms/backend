package com.myme.mywarehome.domains.issue.adapter.out.persistence;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.myme.mywarehome.domains.issue.application.port.in.result.TodayIssueResult;
import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
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

    @Query("SELECT DISTINCT ip FROM IssuePlan ip JOIN FETCH ip.product WHERE ip.issuePlanDate = :date")
    List<IssuePlan> findByIssuePlanDate(@Param("date") LocalDate date);


    @EntityGraph(attributePaths = {"product", "product.company"})
    @Query("""
SELECT new com.myme.mywarehome.domains.issue.application.port.in.result.TodayIssueResult(
    ip.issuePlanId,
    ip.issuePlanCode,
    ip.issuePlanDate,
    CAST(COALESCE((SELECT COUNT(DISTINCT i2.issueId) FROM Issue i2 WHERE i2.issuePlan = ip), 0) AS long),
    CAST(ip.issuePlanItemCount AS long),
    CASE
        WHEN COALESCE((SELECT COUNT(DISTINCT i2.issueId) FROM Issue i2 WHERE i2.issuePlan = ip), 0) = 0 
        THEN 'NOT_STARTED'
        WHEN COALESCE((SELECT COUNT(DISTINCT i2.issueId) FROM Issue i2 WHERE i2.issuePlan = ip), 0) < ip.issuePlanItemCount 
        THEN 'PROCESSING'
        ELSE 'DONE'
    END,
    ip.product.productNumber,
    ip.product.productName,
    ip.product.company.companyId,
    ip.product.company.companyCode,
    ip.product.company.companyName,
    ip.createdAt,
    ip.updatedAt
)
FROM IssuePlan ip
WHERE ip.issuePlanDate >= :selectedDate
""")
    Page<TodayIssueResult> findTodayIssues(LocalDate selectedDate, Pageable pageable);

    @EntityGraph(attributePaths = {"product", "product.company"})
    @Query("""
SELECT new com.myme.mywarehome.domains.issue.application.port.in.result.TodayIssueResult(
    ip.issuePlanId,
    ip.issuePlanCode,
    ip.issuePlanDate,
    CAST(COALESCE((SELECT COUNT(DISTINCT i2.issueId) FROM Issue i2 WHERE i2.issuePlan = ip), 0) AS long),
    CAST(ip.issuePlanItemCount AS long),
    CASE
        WHEN COALESCE((SELECT COUNT(DISTINCT i2.issueId) FROM Issue i2 WHERE i2.issuePlan = ip), 0) = 0 
        THEN 'NOT_STARTED'
        WHEN COALESCE((SELECT COUNT(DISTINCT i2.issueId) FROM Issue i2 WHERE i2.issuePlan = ip), 0) < ip.issuePlanItemCount 
        THEN 'PROCESSING'
        ELSE 'DONE'
    END,
    ip.product.productNumber,
    ip.product.productName,
    ip.product.company.companyId,
    ip.product.company.companyCode,
    ip.product.company.companyName,
    ip.createdAt,
    ip.updatedAt
)
    FROM IssuePlan ip
    WHERE ip.issuePlanId = :issuePlanId
    AND ip.issuePlanDate = :today
    """)
    Optional<TodayIssueResult> findTodayIssueById(
            @Param("issuePlanId") Long issuePlanId,
            @Param("today") LocalDate today
    );
}
