package com.myme.mywarehome.domains.issue.adapter.out.persistence;

import com.myme.mywarehome.domains.issue.application.domain.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface IssueJpaRepository extends JpaRepository<Issue, Long> {

    @Query("SELECT COUNT(i) FROM Issue i WHERE i.issuePlan.issuePlanId = :issuePlanId")
    long countProcessedIssueByIssuePlanId(@Param("issuePlanId") Long issuePlanId);

    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM Issue i WHERE i.stock.stockId = :stockId")
    boolean existsByStockId(@Param("stockId") Long stockId);
 

    @EntityGraph(attributePaths = {"issuePlan", "issuePlan.product", "issuePlan.product.company"})
    @Query("SELECT r FROM Issue r WHERE " +
            "(" +
            "   CASE WHEN (COALESCE(:companyName, '') != '' OR COALESCE(:productName, '') != '') " +
            "   THEN (" +
            "       (COALESCE(:companyName, '') != '' AND r.issuePlan.product.company.companyName LIKE CONCAT('%', :companyName, '%')) " +
            "       OR (COALESCE(:productName, '') != '' AND r.issuePlan.product.productName LIKE CONCAT('%', :productName, '%')) " +
            "   ) " +
            "   ELSE true END" +
            ") " +
            "AND (r.issuePlan.product.company.companyCode LIKE CONCAT('%', COALESCE(:companyCode, ''), '%')) " +
            "AND (r.issuePlan.issuePlanCode LIKE CONCAT('%', COALESCE(:issuePlanCode, ''), '%')) " +
            "AND (CAST(:issuePlanStartDate AS LocalDate) IS NULL OR r.issuePlan.issuePlanDate >= :issuePlanStartDate) " +
            "AND (CAST(:issuePlanEndDate AS LocalDate) IS NULL OR r.issuePlan.issuePlanDate <= :issuePlanEndDate) " +
            "AND (r.issuePlan.product.productNumber LIKE CONCAT('%', COALESCE(:productNumber, ''), '%')) "+
            "AND (r.issueCode LIKE CONCAT('%', COALESCE(:issueCode, ''), '%')) " +
            "AND (CAST(:issueStartDate AS LocalDate) IS NULL OR r.issueDate >= :issueStartDate) " +
            "AND (CAST(:issueEndDate AS LocalDate) IS NULL OR r.issueDate <= :issueEndDate) ")
    Page<Issue> findByConditions(
            @Param("companyCode") String companyCode,
            @Param("companyName") String companyName,
            @Param("issuePlanCode") String issuePlanCode,
            @Param("issuePlanStartDate") LocalDate issuePlanStartDate,
            @Param("issuePlanEndDate") LocalDate issuePlanEndDate,
            @Param("productNumber") String productNumber,
            @Param("productName") String productName,
            @Param("issueCode") String issueCode,
            @Param("issueStartDate") LocalDate issueStartDate,
            @Param("issueEndDate") LocalDate issueEndDate,
            Pageable pageable
    );

    Integer countByIssueDate(LocalDate issueDate);

    @Query("SELECT COUNT(i) FROM Issue i " +
            "WHERE EXTRACT(YEAR FROM i.issueDate) = :#{#targetDate.year} " +
            "AND EXTRACT(MONTH FROM i.issueDate) = :#{#targetDate.monthValue}")
    Integer countByMonth(@Param("targetDate") LocalDate targetDate);

}
