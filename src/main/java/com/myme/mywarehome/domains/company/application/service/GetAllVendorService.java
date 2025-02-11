package com.myme.mywarehome.domains.company.application.service;

import com.myme.mywarehome.domains.company.application.domain.Company;
import com.myme.mywarehome.domains.company.application.port.in.GetAllVendorUseCase;
import com.myme.mywarehome.domains.company.application.port.out.GetAllVendorPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAllVendorService implements GetAllVendorUseCase {
    private final GetAllVendorPort getAllVendorPort;

    @Override
    public Page<Company> getAllVendors(String companyCode, String companyName, Pageable pageable) {
        return getAllVendorPort.findVendors(companyCode, companyName, pageable);
    }
}
