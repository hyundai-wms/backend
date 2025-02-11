package com.myme.mywarehome.domains.company.adapter.out.persistence;

import com.myme.mywarehome.domains.company.application.domain.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyJpaRepository extends JpaRepository<Company, Long> {
    // Todo: mybatis로 변경
    @Query("SELECT c FROM Company c WHERE c.isVendor = true " +
            "AND (c.companyCode LIKE CONCAT('%' , COALESCE(:code, ''), '%')" +
            "OR c.companyName LIKE CONCAT('%' , COALESCE(:name, ''), '%'))")
    Page<Company> findVendorsByConditions(
            @Param("code") String code,
            @Param("name") String name,
            Pageable pageable
    );

    @Query("SELECT c FROM Company c WHERE c.companyId = :companyId AND c.isVendor = true")
    Page<Company> findVendorByCompanyId(
            @Param("companyId") Long companyId,
            Pageable pageable
    );
}


