package com.myme.mywarehome.domains.company.application.port.out;

import com.myme.mywarehome.domains.company.application.domain.Company;
import com.myme.mywarehome.domains.product.application.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetInhousePort {
    Page<Product> findInhouseByConditions (String productNumber, String productName, String applicableEngine, Pageable pageable);

}
