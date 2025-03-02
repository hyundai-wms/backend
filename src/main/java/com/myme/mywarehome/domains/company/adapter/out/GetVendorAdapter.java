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
    public Page<Company> findVendorByCompanyId(Long companyId, Pageable pageable) {
        return companyJpaRepository.findVendorByCompanyId(companyId, pageable);
    }

}
