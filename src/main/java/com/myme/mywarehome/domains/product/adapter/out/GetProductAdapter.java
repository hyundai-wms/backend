package com.myme.mywarehome.domains.product.adapter.out;

import com.myme.mywarehome.domains.product.adapter.out.persistence.ProductJpaRepository;
import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.product.application.port.out.GetProductPort;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetProductAdapter implements GetProductPort {
    private final ProductJpaRepository productJpaRepository;

    @Override
    public Page<Product> readAll(Pageable pageable) {
        return productJpaRepository.findAll(pageable);
    }

    @Override
    public Optional<Product> findByProductNumber(String productNumber) {
        return productJpaRepository.findByProductNumber(productNumber);
    }

    @Override
    public List<Product> findAllByProductNumbers(Set<String> productNumberList) {
        return productJpaRepository.findAllByProductNumberIn(productNumberList);
    }
}
