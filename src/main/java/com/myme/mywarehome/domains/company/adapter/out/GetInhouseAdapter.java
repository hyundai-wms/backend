package com.myme.mywarehome.domains.company.adapter.out;

import com.myme.mywarehome.domains.company.adapter.out.persistence.CompanyJpaRepository;
import com.myme.mywarehome.domains.company.application.domain.Company;
import com.myme.mywarehome.domains.company.application.port.out.GetInhousePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetInhouseAdapter implements GetInhousePort {
    private final CompanyJpaRepository companyJpaRepository;

    @Override
    public Page<Company> findInhouseByConditions(String productNumber, String productName, String applicableEngine, Pageable pageable) {
        return companyJpaRepository.findInhouseByConditions(productNumber, productName, applicableEngine, pageable);
    }

}
