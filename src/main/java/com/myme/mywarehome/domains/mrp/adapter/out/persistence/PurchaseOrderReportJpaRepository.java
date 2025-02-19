package com.myme.mywarehome.domains.mrp.adapter.out.persistence;

import com.myme.mywarehome.domains.mrp.application.domain.PurchaseOrderReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderReportJpaRepository extends JpaRepository<PurchaseOrderReport, Long> {

}
