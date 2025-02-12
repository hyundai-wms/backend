package com.myme.mywarehome.domains.company.adapter.in.web.request;

public record GetInhouseRequest(
        String productNumber,
        String productName,
        String applicableEngine
) {
}
