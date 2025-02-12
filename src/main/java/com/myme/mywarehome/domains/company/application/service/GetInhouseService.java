package com.myme.mywarehome.domains.company.application.service;

import com.myme.mywarehome.domains.company.application.domain.Company;
import com.myme.mywarehome.domains.company.application.port.in.GetInhouseUseCase;
import com.myme.mywarehome.domains.company.application.port.out.GetInhousePort;
import com.myme.mywarehome.domains.product.application.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetInhouseService implements GetInhouseUseCase {
    private final GetInhousePort getInhousePort;

    @Override
    public Page<Company> getInhouses(String productNumber, String productName, String applicableEngine, Pageable pageable) {
        return getInhousePort.findInhouseByConditions(productNumber, productName, applicableEngine, pageable);
    }



}
