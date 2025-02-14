package com.myme.mywarehome.domains.issue.adapter.out.persistence;

import com.myme.mywarehome.domains.issue.application.domain.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IssueJpaRepository extends JpaRepository<Issue, Long> {
    @Query("SELECT COUNT(i) FROM Issue i WHERE i.issuePlan.issuePlanId = :issuePlanId")
    long countProcessedIssueByIssuePlanId(@Param("issuePlanId") Long issuePlanId);

    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM Issue i WHERE i.stock.stockId = :stockId")
    boolean existsByStockId(@Param("stockId") Long stockId);
}
