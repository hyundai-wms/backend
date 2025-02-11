package com.myme.mywarehome.domains.company.adapter.in.web.response;

import com.myme.mywarehome.domains.company.application.domain.Company;
import com.myme.mywarehome.domains.product.application.domain.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public record GetVendorResponse(
        List<VendorSpecificInfo> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isFirst,
        boolean isLast
) {
    public static GetVendorResponse from(Page<Company> page) {
        List<VendorSpecificInfo> vendorList = page.getContent()
                .stream()
                .map(VendorSpecificInfo::from)
                .toList();

        return new GetVendorResponse(
                vendorList,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    public record VendorSpecificInfo(
            Long companyId,
            String companyCode,
            String companyName,
            ContactInfo companyContactInfo,
            List<ProductInfo> productList
    ) {
        public record ContactInfo(
                String fax,
                String phone,
                String email
        ) {
            public static ContactInfo from(Company company) {
                return new ContactInfo(
                        company.getCompanyFax(),
                        company.getCompanyPhone(),
                        company.getCompanyEmail()
                );
            }
        }

        public record ProductInfo(
                String productNumber,
                String productName,
                String applicableEngine,
                int leadTime
        ) {
            public static ProductInfo from(Product product) {
                return new ProductInfo(
                        product.getProductNumber(),
                        product.getProductName(),
                        product.getApplicableEngine(),
                        product.getLeadTime()
                );
            }
        }

        public static VendorSpecificInfo from(Company company) {
            List<ProductInfo> productList = company.getProductList()
                    .stream()
                    .map(ProductInfo::from)
                    .toList();

            return new VendorSpecificInfo(
                    company.getCompanyId(),
                    company.getCompanyCode(),
                    company.getCompanyName(),
                    ContactInfo.from(company),
                    productList
            );
        }
    }
}
