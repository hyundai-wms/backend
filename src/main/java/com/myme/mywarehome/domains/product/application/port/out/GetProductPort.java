package com.myme.mywarehome.domains.product.application.port.out;

import com.myme.mywarehome.domains.product.application.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetProductPort {
    Page<Product> readAll(Pageable pageable);
}
