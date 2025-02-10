package com.myme.mywarehome.domains.issue.adapter.out.persistence;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuePlanJpaRepository extends JpaRepository<IssuePlan, Long> {
}
