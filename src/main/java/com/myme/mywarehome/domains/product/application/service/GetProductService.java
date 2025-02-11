package com.myme.mywarehome.domains.product.application.service;

import com.myme.mywarehome.domains.product.adapter.in.web.request.GetProductRequest;
import com.myme.mywarehome.domains.product.adapter.in.web.response.GetProductResponse;
import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.product.application.port.in.GetProductUseCase;
import com.myme.mywarehome.domains.product.application.port.out.GetProductPort;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetProductService implements GetProductUseCase {
    private final GetProductPort getProductPort;


    @Override
    public Page<Product> readAll(Pageable pageable) {
        return getProductPort.readAll(pageable);  // 엔티티
    }



}
