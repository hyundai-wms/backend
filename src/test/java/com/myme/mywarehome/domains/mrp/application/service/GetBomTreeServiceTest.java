package com.myme.mywarehome.domains.mrp.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.myme.mywarehome.domains.mrp.application.domain.BomTree;
import com.myme.mywarehome.domains.mrp.application.domain.EngineType;
import com.myme.mywarehome.domains.mrp.application.port.out.GetBomTreePort;
import com.myme.mywarehome.domains.product.application.domain.Product;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetBomTreeServiceTest {

    @Mock
    private GetBomTreePort getBomTreePort;

    private GetBomTreeService getBomTreeService;

    private List<BomTree> mockBomTrees;
    private Product parentProduct;
    private Product childProduct;

    @BeforeEach
    void setUp() {
        getBomTreeService = new GetBomTreeService(getBomTreePort);

        // Mock Product 데이터 설정
        parentProduct = Product.builder()
                .productId(1L)
                .productName("완제품A")
                .build();

        childProduct = Product.builder()
                .productId(2L)
                .productName("부품B")
                .build();

        // Mock BomTree 데이터 설정
        mockBomTrees = List.of(
                BomTree.builder()
                        .parentProduct(parentProduct)
                        .childProduct(childProduct)
                        .childCompositionRatio(70)
                        .build()
        );
    }

    @Test
    @DisplayName("kappa 엔진 이름으로 BOM 트리를 조회한다")
    void getBomTreeByEngine_withValidEngineName_shouldReturnBomTree() {
        // given
        String engineName = "kappa";
        String dbEngineName = EngineType.convertToDbName(engineName);

        when(getBomTreePort.findAllByApplicableEngine(dbEngineName))
                .thenReturn(mockBomTrees);

        // when
        List<BomTree> result = getBomTreeService.getBomTreeByEngine(engineName);

        // then
        verify(getBomTreePort).findAllByApplicableEngine(dbEngineName);

        assertThat(result)
                .hasSize(1)
                .usingRecursiveComparison()
                .isEqualTo(mockBomTrees);

        // BOM 트리 구조 검증
        BomTree bomTree = result.get(0);
        assertThat(bomTree)
                .satisfies(bt -> {
                    assertThat(bt.getParentProduct())
                            .satisfies(parent -> {
                                assertThat(parent.getProductId()).isEqualTo(1L);
                                assertThat(parent.getProductName()).isEqualTo("완제품A");
                            });

                    assertThat(bt.getChildProduct())
                            .satisfies(child -> {
                                assertThat(child.getProductId()).isEqualTo(2L);
                                assertThat(child.getProductName()).isEqualTo("부품B");
                            });

                    assertThat(bt.getChildCompositionRatio()).isEqualTo(70);
                });
    }
}