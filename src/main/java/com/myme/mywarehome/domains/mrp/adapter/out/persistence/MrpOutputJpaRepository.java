package com.myme.mywarehome.domains.mrp.adapter.out.persistence;

import com.myme.mywarehome.domains.mrp.application.domain.MrpOutput;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MrpOutputJpaRepository extends JpaRepository<MrpOutput, Long> {

    @Modifying
    @Query("UPDATE MrpOutput m SET m.canOrder = false WHERE m.canOrder = true")
    void deactivateAllPreviousOrders();
}
