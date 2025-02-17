package com.myme.mywarehome.domains.mrp.adapter.out.persistence;

import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRecordJpaRepository extends JpaRepository<InventoryRecord, Long> {

}
