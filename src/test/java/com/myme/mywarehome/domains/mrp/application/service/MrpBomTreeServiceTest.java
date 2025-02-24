package com.myme.mywarehome.domains.mrp.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.myme.mywarehome.domains.mrp.application.domain.BomTree;
import com.myme.mywarehome.domains.mrp.application.port.in.command.MrpInputCommand;
import com.myme.mywarehome.domains.mrp.application.port.out.GetBomTreePort;
import com.myme.mywarehome.domains.mrp.application.service.dto.UnifiedBomDataDto;
import com.myme.mywarehome.domains.product.application.domain.Product;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MrpBomTreeServiceTest {

    @Mock
    private GetBomTreePort getBomTreePort;

    private MrpBomTreeService mrpBomTreeService;
    private Product engine;
    private Product component;
    private List<BomTree> bomTrees;
    private LocalDate dueDate;

    @BeforeEach
    void setUp() {
        mrpBomTreeService = new MrpBomTreeService(getBomTreePort);

        // 테스트 데이터 설정
        engine = Product.builder()
                .productId(1L)
                .productNumber("ENGINE-001")
                .productName("Test Engine")
                .build();

        component = Product.builder()
                .productId(2L)
                .productNumber("COMP-001")
                .productName("Test Component")
                .build();

        bomTrees = List.of(
                BomTree.builder()
                        .parentProduct(engine)
                        .childProduct(component)
                        .childCompositionRatio(2)
                        .build()
        );

        dueDate = LocalDate.now().plusDays(30);
    }

    @Test
    @DisplayName("BOM 트리를 통합하여 가상 루트를 생성한다")
    void createUnifiedBomTree_withEngineCount_shouldCreateVirtualRoot() {
        // given
        Map<String, Integer> engineCountMap = Map.of("kappa", 1);
        MrpInputCommand command = new MrpInputCommand(engineCountMap, dueDate);

        when(getBomTreePort.findAllByApplicableEngine(anyString()))
                .thenReturn(bomTrees);

        // when
        UnifiedBomDataDto result = mrpBomTreeService.createUnifiedBomTree(command);

        // then
        assertThat(result.virtualRoot().getProductId()).isEqualTo(-1L);
        assertThat(result.virtualRoot().getProductNumber()).isEqualTo("VIRTUAL-ROOT");

        assertThat(result.unifiedBomTree())
                .hasSize(5)  // 원본 BOM tree + 가상 루트 연결
                .satisfies(trees -> {
                    assertThat(trees.stream()
                            .filter(tree -> tree.getParentProduct().getProductId().equals(-1L))
                            .findFirst())
                            .isPresent()
                            .get()
                            .satisfies(rootConnection -> {
                                assertThat(rootConnection.getChildProduct()).isEqualTo(engine);
                                assertThat(rootConnection.getChildCompositionRatio()).isEqualTo(1);
                            });
                });

        assertThat(result.bomTreeMap())
                .containsKey(-1L)  // 가상 루트
                .containsKey(1L);  // 엔진
    }

    @Test
    @DisplayName("빈 BOM 트리는 제외하고 통합한다")
    void createUnifiedBomTree_withEmptyBom_shouldExcludeEmpty() {
        // given
        Map<String, Integer> engineCountMap = Map.of(
                "kappa", 1,
                "gamma", 1
        );
        MrpInputCommand command = new MrpInputCommand(engineCountMap, dueDate);

        // kappa 엔진에 대해서만 BOM 트리 반환
        when(getBomTreePort.findAllByApplicableEngine(anyString()))
                .thenReturn(List.of())
                .thenReturn(bomTrees);

        // when
        UnifiedBomDataDto result = mrpBomTreeService.createUnifiedBomTree(command);

        // then
        assertThat(result.unifiedBomTree()).hasSize(5);  // 원본 BOM tree + 가상 루트 연결
        assertThat(result.bomTreeMap())
                .containsOnlyKeys(-1L, 1L)  // 가상 루트와 kappa 엔진만 포함
                .doesNotContainKey(4L);      // gamma 엔진 제외
    }
}