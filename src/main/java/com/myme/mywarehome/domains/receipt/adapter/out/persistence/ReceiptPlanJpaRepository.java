package com.myme.mywarehome.domains.receipt.adapter.out.persistence;

import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptPlanJpaRepository extends JpaRepository<ReceiptPlan, Long> {
}
