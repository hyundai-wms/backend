package com.myme.mywarehome.domains.product.adapter.in.web.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public record GetProductRequest(
        Integer page,
        Integer size,
        String sort
) {
    public Pageable toPageable() {
        return PageRequest.of(
                page == null ? 0 : page,
                size == null ? 10 : size,
                Sort.by(Sort.Direction.DESC, sort == null ? "createdAt" : sort)
        );
    }
}