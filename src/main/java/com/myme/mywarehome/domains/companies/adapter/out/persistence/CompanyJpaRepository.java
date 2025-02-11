package com.myme.mywarehome.domains.companies.adapter.out.persistence;

import com.myme.mywarehome.domains.companies.application.domain.Company;
import com.myme.mywarehome.domains.product.application.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyJpaRepository extends JpaRepository<Company, Long> {

}
