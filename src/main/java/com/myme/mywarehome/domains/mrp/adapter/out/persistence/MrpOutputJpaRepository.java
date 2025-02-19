package com.myme.mywarehome.domains.mrp.adapter.out.persistence;

import com.myme.mywarehome.domains.mrp.application.domain.MrpOutput;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MrpOutputJpaRepository extends JpaRepository<MrpOutput, Long> {

    @Modifying
    @Query("UPDATE MrpOutput m SET m.canOrder = false WHERE m.canOrder = true")
    void deactivateAllPreviousOrders();

    @Query(value = "SELECT m FROM MrpOutput m " +
            "WHERE (CAST(:startDate AS date) IS NULL OR m.orderedDate >= :startDate) " +
            "AND (CAST(:endDate AS date) IS NULL OR m.orderedDate <= :endDate) " +
            "AND (CAST(:isOrdered AS boolean) IS NULL OR m.canOrder = :isOrdered)")
    Page<MrpOutput> findAllByConditions(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("isOrdered") Boolean isOrdered,
            Pageable pageable
    );

}
