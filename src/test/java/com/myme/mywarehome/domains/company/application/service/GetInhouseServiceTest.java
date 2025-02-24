package com.myme.mywarehome.domains.company.application.service;

import com.myme.mywarehome.domains.company.application.port.out.GetInhousePort;
import com.myme.mywarehome.domains.product.application.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GetInhouseServiceTest {

    @InjectMocks
    private GetInhouseService getInhouseService;

    @Mock
    private GetInhousePort getInhousePort;

    @Test
    @DisplayName("제품 번호, 이름, 적용 엔진으로 사내 제작 제품을 검색한다")
    void getInhouses_WithAllSearchConditions_ReturnsFilteredProducts() {
        // given
        String productNumber = "PROD001";
        String productName = "테스트제품";
        String applicableEngine = "엔진A";
        Pageable pageable = PageRequest.of(0, 10);

        Product product = Product.builder()
                .productNumber(productNumber)
                .productName(productName)
                .applicableEngine(applicableEngine)
                .build();
        Page<Product> expectedPage = new PageImpl<>(List.of(product));

        given(getInhousePort.findInhouseByConditions(
                eq(productNumber),
                eq(productName),
                eq(applicableEngine),
                any(Pageable.class)))
                .willReturn(expectedPage);

        // when
        Page<Product> actualPage = getInhouseService.getInhouses(
                productNumber,
                productName,
                applicableEngine,
                pageable);

        // then
        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getContent()).hasSize(1);
        assertThat(actualPage.getContent().get(0))
                .extracting("productNumber", "productName", "applicableEngine")
                .containsExactly(productNumber, productName, applicableEngine);

        verify(getInhousePort).findInhouseByConditions(
                productNumber,
                productName,
                applicableEngine,
                pageable);
    }

    @Test
    @DisplayName("검색 조건 없이 모든 사내 제작 제품을 조회한다")
    void getInhouses_WithoutSearchConditions_ReturnsAllProducts() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = new PageImpl<>(List.of(
                Product.builder()
                        .productNumber("PROD001")
                        .productName("제품1")
                        .applicableEngine("엔진A")
                        .build(),
                Product.builder()
                        .productNumber("PROD002")
                        .productName("제품2")
                        .applicableEngine("엔진B")
                        .build()
        ));

        given(getInhousePort.findInhouseByConditions(
                eq(null),
                eq(null),
                eq(null),
                any(Pageable.class)))
                .willReturn(expectedPage);

        // when
        Page<Product> actualPage = getInhouseService.getInhouses(
                null,
                null,
                null,
                pageable);

        // then
        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getContent()).hasSize(2);
        assertThat(actualPage.getContent())
                .extracting("productNumber", "productName", "applicableEngine")
                .containsExactly(
                        tuple("PROD001", "제품1", "엔진A"),
                        tuple("PROD002", "제품2", "엔진B")
                );

        verify(getInhousePort).findInhouseByConditions(null, null, null, pageable);
    }
}