package com.myme.mywarehome.domains.mrp.adapter.out.persistence;

import com.myme.mywarehome.domains.mrp.application.domain.BomTree;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BomTreeJpaRepository extends JpaRepository<BomTree, Long> {
}
