package com.myme.mywarehome.domains.companies.adapter.in.web.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record GetVendorRequest(
        Integer page,
        Integer size,
        String sort,
        String companyCode,
        String companyName
) {
    public Pageable toPageable() {
        return PageRequest.of(
                page == null ? 0 : page,
                size == null ? 10 : size,
                Sort.by(Sort.Direction.DESC, sort == null ? "createdAt" : sort)
        );
    }

}
