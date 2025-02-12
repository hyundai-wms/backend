package com.myme.mywarehome.domains.product.application.port.out;

import com.myme.mywarehome.domains.product.application.domain.Product;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface GetProductPort {
    Page<Product> readAll(Pageable pageable);
    Optional<Product> findByProductNumber(String productNumber);
    List<Product> findAllByProductNumbers(Set<String> productNumberList);
}
