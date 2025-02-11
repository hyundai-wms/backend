package com.myme.mywarehome.domains.product.application.port.out;

import com.myme.mywarehome.domains.product.application.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface GetProductPort {
    Page<Product> readAll(Pageable pageable);
    Optional<Product> findByProductNumber(String productNumber);
}
