package com.myme.mywarehome.domains.product.adapter.in.web;

import com.myme.mywarehome.domains.product.adapter.in.web.request.GetProductRequest;
import com.myme.mywarehome.domains.product.adapter.in.web.response.GetProductResponse;
import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.product.application.port.in.GetProductUseCase;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final GetProductUseCase getProductUseCase;

    @GetMapping
    public CommonResponse<GetProductResponse> getProducts(@Valid GetProductRequest getProductRequest) {
        return CommonResponse.from(
                GetProductResponse.of(getProductUseCase.readAll(getProductRequest.toPageable()))
        );
    }
}
