package com.myme.mywarehome.domains.company.adapter.out;

import com.myme.mywarehome.domains.company.adapter.out.persistence.CompanyJpaRepository;
import com.myme.mywarehome.domains.company.application.domain.Company;
import com.myme.mywarehome.domains.company.application.port.out.GetVendorPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetVendorAdapter implements GetVendorPort {
    private final CompanyJpaRepository companyJpaRepository;

    @Override
    public Page<Company> findVendors(String companyCode, String companyName, Pageable pageable) {

        return companyJpaRepository.findVendorsByConditions(companyCode, companyName, pageable);
    }
}
