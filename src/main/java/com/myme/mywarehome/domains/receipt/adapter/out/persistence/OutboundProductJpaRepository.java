package com.myme.mywarehome.domains.receipt.adapter.out.persistence;

import com.myme.mywarehome.domains.receipt.application.domain.OutboundProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboundProductJpaRepository extends JpaRepository<OutboundProduct, Long> {
    long countByReceiptPlanId(Long receiptPlanId);
    boolean existsByOutboundProductId(String outboundProductId);

    @Query("SELECT op.outboundProductId FROM OutboundProduct op " +
            "WHERE op.receiptPlanId = :receiptPlanId " +
            "ORDER BY op.outboundProductId")
    List<String> findOutboundProductIdsByReceiptPlanId(@Param("receiptPlanId") Long receiptPlanId);
}
