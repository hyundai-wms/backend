package com.myme.mywarehome.domains.company.adapter.in.web.response;

import com.myme.mywarehome.domains.company.application.domain.Company;
import com.myme.mywarehome.domains.product.application.domain.Product;
import java.util.List;
import org.springframework.data.domain.Page;

public record GetInhouseResponse(
        List<InhouseInfo> content,
        int pageNumber,
        int pageSize,
        long totalItems,
        int totalPages,
        boolean isFirst,
        boolean isLast
) {
    public record InhouseInfo(
            String productNumber,
            String productName,
            String applicableEngine,
            int leadTime,
            int eachCount
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

    public static GetInhouseResponse from(Page<Company> page) {
        List<InhouseInfo> content = page.getContent()
                .stream()
                .flatMap(company -> company.getProductList().stream())
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
