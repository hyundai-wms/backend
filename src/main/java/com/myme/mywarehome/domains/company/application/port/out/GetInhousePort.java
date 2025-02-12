package com.myme.mywarehome.domains.company.application.port.out;

import com.myme.mywarehome.domains.company.application.domain.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetInhousePort {
    Page<Company> findInhouseByConditions (String productNumber, String productName, String applicableEngine, Pageable pageable);

}
