package com.myme.mywarehome.domains.companies.application.service;

import com.myme.mywarehome.domains.companies.application.domain.Company;
import com.myme.mywarehome.domains.companies.application.port.in.GetVendorUseCase;
import com.myme.mywarehome.domains.companies.application.port.out.GetVendorPort;
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
    public Page<Company> getVendors(Pageable pageable) {return getVendorPort.findVendors(pageable);}
}
