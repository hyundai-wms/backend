package com.myme.mywarehome.domains.company.application.port.out;

import com.myme.mywarehome.domains.company.application.domain.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetVendorPort {
    Page<Company> findVendorByCompanyId(Long companyId, Pageable pageable);
}
