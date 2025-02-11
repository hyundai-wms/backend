package com.myme.mywarehome.domains.companies.adapter.in.web.response;

import com.myme.mywarehome.domains.companies.application.domain.Company;
import org.springframework.data.domain.Page;

import java.util.List;

public record GetVendorResponse(
        List<VendorInfo> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isFirst,
        boolean isLast
) {
    public record VendorInfo(
            Long companyId,
            String companyCode,
            String companyName,
            ContactInfo companyContactInfo  // 변경된 부분
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

        public static VendorInfo from(Company company) {
            return new VendorInfo(
                    company.getCompanyId(),
                    company.getCompanyCode(),
                    company.getCompanyName(),
                    ContactInfo.from(company)  // 변경된 부분
            );
        }
    }

    public static GetVendorResponse of(Page<Company> page) {
        List<VendorInfo> vendorInfoList = page.getContent().stream()
                .map(VendorInfo::from)
                .toList();

        return new GetVendorResponse(
                vendorInfoList,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}