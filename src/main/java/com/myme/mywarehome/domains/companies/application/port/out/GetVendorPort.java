package com.myme.mywarehome.domains.companies.application.port.out;

import com.myme.mywarehome.domains.companies.application.domain.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetVendorPort {
    Page<Company> getVendors(Pageable pageable);
}
