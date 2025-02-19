package com.myme.mywarehome.domains.company.adapter.in.web.response;

import com.myme.mywarehome.domains.company.application.domain.Company;
import com.myme.mywarehome.domains.product.application.domain.Product;
import java.util.List;
import org.springframework.data.domain.Page;

public record GetInhouseResponse(
        List<InhouseInfo> content,
        Integer pageNumber,
        Integer pageSize,
        Long totalElements,
        Integer totalPages,
        Boolean isFirst,
        Boolean isLast
) {
    public record InhouseInfo(
            String productNumber,
            String productName,
            String applicableEngine,
            Integer leadTime,
            Integer eachCount
    ) {
        public static InhouseInfo from(Product product) {
            return new InhouseInfo(
                    product.getProductNumber(),
                    product.getProductName(),
                    product.getApplicableEngine(),
                    product.getLeadTime(),
                    product.getEachCount()
            );
        }
    }

    public static GetInhouseResponse from(Page<Product> page) {
        List<InhouseInfo> content = page.getContent()
                .stream()
                .map(InhouseInfo::from)
                .toList();

        return new GetInhouseResponse(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }


}
