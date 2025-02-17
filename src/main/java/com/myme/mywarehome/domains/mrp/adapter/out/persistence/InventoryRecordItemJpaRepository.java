package com.myme.mywarehome.domains.mrp.adapter.out.persistence;

import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecordItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRecordItemJpaRepository extends JpaRepository<InventoryRecordItem, Long> {

}
