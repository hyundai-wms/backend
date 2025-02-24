package com.myme.mywarehome.domains.issue.application.service;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.in.command.IssuePlanCommand;
import com.myme.mywarehome.domains.issue.application.port.out.CreateIssuePlanPort;
import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.product.application.exception.ProductNotFoundException;
import com.myme.mywarehome.domains.product.application.port.out.GetProductPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateIssuePlanServiceTest {

    @InjectMocks
    private CreateIssuePlanService createIssuePlanService;

    @Mock
    private CreateIssuePlanPort createIssuePlanPort;

    @Mock
    private GetProductPort getProductPort;

    private Product product;
    private IssuePlanCommand command;
    private IssuePlan issuePlan;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();

        product = Product.builder()
                .productNumber("TEST001")
                .productName("테스트 상품")
                .build();

        command = new IssuePlanCommand(
                product.getProductNumber(),
                10,
                today
        );

        issuePlan = IssuePlan.builder()
                .issuePlanId(1L)
                .issuePlanItemCount(command.itemCount())
                .issuePlanDate(command.issuePlanDate())
                .product(product)
                .build();
    }

    @Test
    @DisplayName("출고 계획을 생성한다")
    void create_WithValidCommand_ReturnsIssuePlan() {
        // given
        when(getProductPort.findByProductNumber(command.productNumber()))
                .thenReturn(Optional.of(product));
        when(createIssuePlanPort.create(any(IssuePlan.class)))
                .thenReturn(issuePlan);

        // when
        IssuePlan result = createIssuePlanService.create(command);

        // then
        assertThat(result.getIssuePlanItemCount()).isEqualTo(command.itemCount());
        assertThat(result.getIssuePlanDate()).isEqualTo(command.issuePlanDate());
        assertThat(result.getProduct()).isEqualTo(product);
        assertThat(result.getIssuePlanCode()).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 상품으로 출고 계획 생성 시 예외가 발생한다")
    void create_WithNonExistentProduct_ThrowsException() {
        // given
        when(getProductPort.findByProductNumber(command.productNumber()))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> createIssuePlanService.create(command))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DisplayName("여러 출고 계획을 한번에 생성한다")
    void createBulk_WithValidCommands_ReturnsIssuePlans() {
        // given
        List<IssuePlanCommand> commands = List.of(command);
        Set<String> productNumbers = Set.of(product.getProductNumber());
        List<Product> products = List.of(product);
        List<IssuePlan> expectedIssuePlans = List.of(issuePlan);

        when(getProductPort.findAllByProductNumbers(productNumbers))
                .thenReturn(products);
        when(createIssuePlanPort.createBulk(any()))
                .thenReturn(expectedIssuePlans);

        // when
        List<IssuePlan> results = createIssuePlanService.createBulk(commands);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getIssuePlanItemCount()).isEqualTo(command.itemCount());
        assertThat(results.get(0).getIssuePlanDate()).isEqualTo(command.issuePlanDate());
        assertThat(results.get(0).getProduct()).isEqualTo(product);
        assertThat(results.get(0).getIssuePlanCode()).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 상품이 포함된 벌크 생성 요청 시 예외가 발생한다")
    void createBulk_WithNonExistentProduct_ThrowsException() {
        // given
        List<IssuePlanCommand> commands = List.of(command);
        Set<String> productNumbers = Set.of(product.getProductNumber());

        when(getProductPort.findAllByProductNumbers(productNumbers))
                .thenReturn(List.of());

        // when & then
        assertThatThrownBy(() -> createIssuePlanService.createBulk(commands))
                .isInstanceOf(ProductNotFoundException.class);
    }
}