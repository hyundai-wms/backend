package com.myme.mywarehome.domains.companies.adapter.out.persistence;

import com.myme.mywarehome.domains.companies.application.domain.Company;
import com.myme.mywarehome.domains.product.application.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CompanyJpaRepository extends JpaRepository<Company, Long> {
    // Todo: mybatis로 변경
    @Query("SELECT c FROM Company c WHERE c.isVendor = true " +
            "AND c.companyCode LIKE :code " +
            "AND c.companyName LIKE :name")
    Page<Company> findVendorsByConditions(
            @Param("code") String code,
            @Param("name") String name,
            Pageable pageable
    );
}
