package com.myme.mywarehome.domains.receipt.adapter.out.persistence;

import com.myme.mywarehome.domains.receipt.application.domain.OutboundProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboundProductJpaRepository extends JpaRepository<OutboundProduct, Long> {
    long countByReceiptPlanId(Long receiptPlanId);
    boolean existsByOutboundProductId(String outboundProductId);
}
