package com.myme.mywarehome.domains.product.application.port.in;

import com.myme.mywarehome.domains.product.adapter.in.web.request.GetProductRequest;
import com.myme.mywarehome.domains.product.adapter.in.web.response.GetProductResponse;
import com.myme.mywarehome.domains.product.application.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetProductUseCase {
    Page<Product> readAll(Pageable pageable);
}
