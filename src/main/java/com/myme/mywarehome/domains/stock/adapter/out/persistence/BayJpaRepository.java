package com.myme.mywarehome.domains.stock.adapter.out.persistence;

import com.myme.mywarehome.domains.stock.application.domain.Bay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BayJpaRepository extends JpaRepository<Bay, Long> {

}
