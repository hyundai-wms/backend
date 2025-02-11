package com.myme.mywarehome.domains.company.application.service;

import com.myme.mywarehome.domains.company.application.domain.Company;
import com.myme.mywarehome.domains.company.application.port.in.GetVendorUseCase;
import com.myme.mywarehome.domains.company.application.port.out.GetVendorPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetVendorService implements GetVendorUseCase {
    private final GetVendorPort getVendorPort;

    @Override
    public Page<Company> getSpecificVendors(Long companyId, Pageable pageable) {
        return getVendorPort.findVendorByCompanyId(companyId, pageable);
    }
}
