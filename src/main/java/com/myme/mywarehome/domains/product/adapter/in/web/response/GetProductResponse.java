package com.myme.mywarehome.domains.product.adapter.in.web.response;

import com.myme.mywarehome.domains.product.application.domain.Product;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public record GetProductResponse(
        List<ProductInfo> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isFirst,
        boolean isLast

) {

    public record ProductInfo(
            Long productId,
            String productNumber,
            String productName,
            Integer eachCount,
            Integer safeItemCount,
            Integer leadTime,
            String applicableEngine,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    )
    {
        public static ProductInfo from(Product product) {
            return new ProductInfo(
                    product.getProductId(),
                    product.getProductNumber(),
                    product.getProductName(),
                    product.getEachCount(),
                    product.getSafeItemCount(),
                    product.getLeadTime(),
                    product.getApplicableEngine(),
                    product.getCreatedAt(),
                    product.getUpdatedAt()
            );
        }
    }
    public static GetProductResponse of(Page<Product> productPage) {
        List<ProductInfo> content = productPage.getContent().stream()
                .map(ProductInfo::from)
                .toList();

        return new GetProductResponse(
                content,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isFirst(),
                productPage.isLast()
        );
    }
}
