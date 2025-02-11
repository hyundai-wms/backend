package com.myme.mywarehome.domains.companies.adapter.out;

import com.myme.mywarehome.domains.companies.adapter.out.persistence.CompanyJpaRepository;
import com.myme.mywarehome.domains.companies.application.domain.Company;
import com.myme.mywarehome.domains.companies.application.port.out.GetVendorPort;
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
        // null 체크는 어댑터에서 처리
        String codePattern = companyCode == null ? "%" : "%" + companyCode + "%";
        String namePattern = companyName == null ? "%" : "%" + companyName + "%";

        return companyJpaRepository.findVendorsByConditions(codePattern, namePattern, pageable);
    }
}
