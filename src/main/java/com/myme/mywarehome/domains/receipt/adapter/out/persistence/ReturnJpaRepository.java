package com.myme.mywarehome.domains.receipt.adapter.out.persistence;

import com.myme.mywarehome.domains.receipt.application.domain.Return;
import com.myme.mywarehome.domains.statistic.adapter.in.web.response.GetMrpStatisticResponse;
import com.myme.mywarehome.domains.statistic.adapter.in.web.response.GetMrpStatisticResponse.CompanyReturnCount;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnJpaRepository extends JpaRepository<Return, Long> {
    @Query("SELECT COUNT(r) FROM Return r WHERE r.receiptPlan.receiptPlanId = :receiptPlanId")
    long countByReceiptPlanId(@Param("receiptPlanId") Long receiptPlanId);

    @Query("SELECT COUNT(r) FROM Return r " +
            "WHERE EXTRACT(YEAR FROM r.returnDate) = :#{#targetDate.year} " +
            "AND EXTRACT(MONTH FROM r.returnDate) = :#{#targetDate.monthValue}")
    Integer countByMonth(@Param("targetDate") LocalDate targetDate);

    List<Return> findTop10ByOrderByCreatedAtDesc();

    @Query("""
    SELECT c.companyName as companyName, COUNT(r) as returnCount
    FROM Return r
    JOIN r.receiptPlan rp
    JOIN rp.product p
    JOIN p.company c
    GROUP BY c.companyName
    ORDER BY COUNT(r) DESC
    LIMIT 5
    """)
    List<Object[]> findTop5CompanyReturnCounts();

    @Query("SELECT COUNT(r) FROM Return r")
    Integer findTotalReturnCount();

}
