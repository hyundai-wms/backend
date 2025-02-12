package com.myme.mywarehome.domains.product.adapter.out.persistence;

import com.myme.mywarehome.domains.product.application.domain.Product;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product, Long> {
    //Page<Product> findAll(Pageable pageable);

    Optional<Product> findByProductNumber(String productNumber);

    @Query("SELECT p FROM Product p WHERE p.productNumber IN :productNumberList")
    List<Product> findAllByProductNumberIn(@Param("productNumberList") Set<String> productNumberList);
}
