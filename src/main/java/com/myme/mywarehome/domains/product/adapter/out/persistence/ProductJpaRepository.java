package com.myme.mywarehome.domains.product.adapter.out.persistence;

import com.myme.mywarehome.domains.product.application.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product, Long> {
    //Page<Product> findAll(Pageable pageable);

    Optional<Product> findByProductNumber(String productNumber);
}
