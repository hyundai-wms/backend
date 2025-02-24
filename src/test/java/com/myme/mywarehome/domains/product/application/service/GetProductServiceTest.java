package com.myme.mywarehome.domains.product.application.service;

import com.myme.mywarehome.domains.product.adapter.out.persistence.ProductJpaRepository;
import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.product.application.port.out.GetProductPort;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProductServiceTest {
    @InjectMocks
    private GetProductService getProductService;

    @Mock
    private GetProductPort getProductPort;

    private List<Product> testProducts;
    private Page<Product> testProductPage;
    private Pageable defaultPageable;

    @BeforeEach
    void setUp() { // 기본 setup
        // 기본 Pageable 설정
        defaultPageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        // Product 데이터
        Product kappaEngine = Product.builder()
                .productId(1L)
                .productNumber("10000-03P00")
                .productName("Kappa 엔진")
                .eachCount(1)
                .leadTime(6)
                .build();

        Product gammaEngine = Product.builder()
                .productId(2L)
                .productNumber("10000-04P00")
                .productName("Gamma 엔진")
                .eachCount(1)
                .leadTime(6)
                .build();

        testProducts = Arrays.asList(kappaEngine, gammaEngine);
        testProductPage = new PageImpl<>(testProducts, defaultPageable, testProducts.size());
    }


    @Test
    @DisplayName("유효한 페이징 요청으로 상품 목록 조회 성공")
    void readAllProducts_withValidPageable_success() {
        // given
        when(getProductPort.readAll(defaultPageable)).thenReturn(testProductPage);

        // when
        Page<Product> actualPage = getProductService.readAll(defaultPageable);

        // then
        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getTotalElements()).isEqualTo(2);
        assertThat(actualPage.getContent().get(0).getProductNumber()).isEqualTo("10000-03P00");
        assertThat(actualPage.getContent().get(1).getProductNumber()).isEqualTo("10000-04P00");

        verify(getProductPort).readAll(defaultPageable);
    }



    @Test
    @DisplayName("데이터 없을 때 빈페이지 반환 성공")
        void readAllProducts_withNoExistingData_returnsEmptyPage(){
            // given
            Page<Product> emptyPage = new PageImpl<>(Collections.emptyList(), defaultPageable, 0);
            when(getProductPort.readAll(defaultPageable)).thenReturn(emptyPage);

            // when
            Page<Product> actualPage = getProductService.readAll(defaultPageable);

            // then
            assertThat(actualPage).isNotNull();
            assertThat(actualPage.getContent().size()).isEqualTo(0);
            assertThat(actualPage.getTotalElements()).isZero();

            verify(getProductPort).readAll(defaultPageable);

    }

    @Test
    @DisplayName("존재하지 않는 페이지 요청시 빈 페이지 반환")
    void readAllProducts_withNonExistentPage_returnsEmptyPage() {
        // given
        Pageable nonExistentPage = PageRequest.of(999, 10);

        // 빈 페이지 반환
        Page<Product> expectedPage = new PageImpl<>(
                Collections.emptyList(),
                nonExistentPage,
                0
        );
        when(getProductPort.readAll(nonExistentPage)).thenReturn(expectedPage);

        // when
        Page<Product> actualPage = getProductService.readAll(nonExistentPage);

        // then
        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getContent().size()).isEqualTo(0);
        assertThat(actualPage.getTotalElements()).isEqualTo(0);
        assertThat(actualPage.getTotalPages()).isEqualTo(0);

        verify(getProductPort).readAll(nonExistentPage);
    }


}








