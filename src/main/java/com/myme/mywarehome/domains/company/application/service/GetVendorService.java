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
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetVendorService implements GetVendorUseCase {
    private final GetVendorPort getVendorPort;

    @Override
    public Page<Company> getVendors(String companyCode, String companyName, Pageable pageable) {
        return getVendorPort.findVendors(companyCode, companyName, pageable);
    }
}
