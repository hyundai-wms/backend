package com.myme.mywarehome.domains.mrp.adapter.out.persistence;

import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecordItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRecordItemJpaRepository extends JpaRepository<InventoryRecordItem, Long> {
    @Query("SELECT iri FROM InventoryRecordItem iri " +
            "JOIN iri.product p " +
            "WHERE iri.inventoryRecord.inventoryRecordId = :inventoryRecordId " +
            "AND (COALESCE(:productNumber, '') = '' OR p.productNumber LIKE CONCAT('%', :productNumber, '%') " +
            "     OR COALESCE(:productName, '') = '' OR p.productName LIKE CONCAT('%', :productName, '%'))")
    Page<InventoryRecordItem> findByConditions(
            @Param("inventoryRecordId") Long inventoryRecordId,
            @Param("productNumber") String productNumber,
            @Param("productName") String productName,
            Pageable pageable
    );
}
