package com.myme.mywarehome.domains.companies.application.port.in;

import com.myme.mywarehome.domains.companies.application.domain.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetVendorUseCase {
    Page<Company> getVendors(Pageable pageable);
}
