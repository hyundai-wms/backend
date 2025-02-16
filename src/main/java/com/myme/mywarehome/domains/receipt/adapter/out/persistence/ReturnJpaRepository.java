package com.myme.mywarehome.domains.receipt.adapter.out.persistence;

import com.myme.mywarehome.domains.receipt.application.domain.Return;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnJpaRepository extends JpaRepository<Return, Long> {
    @Query("SELECT COUNT(r) FROM Return r WHERE r.receiptPlan.receiptPlanId = :receiptPlanId")
    long countByReceiptPlanId(@Param("receiptPlanId") Long receiptPlanId);
}
