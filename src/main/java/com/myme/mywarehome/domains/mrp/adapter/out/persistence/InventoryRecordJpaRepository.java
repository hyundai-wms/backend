package com.myme.mywarehome.domains.mrp.adapter.out.persistence;

import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecord;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRecordJpaRepository extends JpaRepository<InventoryRecord, Long> {

    @Query(value = "SELECT ir FROM InventoryRecord ir " +
            "WHERE (CAST(:startDate AS date) IS NULL OR CAST(ir.stockStatusAt AS date) >= :startDate) " +
            "AND (CAST(:endDate AS date) IS NULL OR CAST(ir.stockStatusAt AS date) <= :endDate)")
    Page<InventoryRecord> findByConditions(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );
}
