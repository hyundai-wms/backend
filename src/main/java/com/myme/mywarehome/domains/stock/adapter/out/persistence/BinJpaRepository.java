package com.myme.mywarehome.domains.stock.adapter.out.persistence;

import com.myme.mywarehome.domains.stock.application.domain.Bin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BinJpaRepository extends JpaRepository<Bin, Long> {

}
