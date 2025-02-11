package com.myme.mywarehome.domains.company.application.port.in;

import com.myme.mywarehome.domains.company.application.domain.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetVendorUseCase {
    Page<Company> getVendors(String companyCode, String companyName, Pageable pageable);
}
