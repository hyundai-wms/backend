package com.myme.mywarehome.domains.product.application.service;

import com.myme.mywarehome.domains.product.adapter.out.persistence.ProductJpaRepository;
import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.product.application.port.out.GetProductPort;
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

    }



//
//
//    @Test
//    @DisplayName("빈 페이지를 반환할 수 있다")




}