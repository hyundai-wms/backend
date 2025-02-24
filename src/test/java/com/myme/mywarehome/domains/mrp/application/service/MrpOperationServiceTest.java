package com.myme.mywarehome.domains.mrp.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecord;
import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecordItem;
import com.myme.mywarehome.domains.mrp.application.exception.MrpCalculationInProgressException;
import com.myme.mywarehome.domains.mrp.application.port.in.CreateInventoryRecordUseCase;
import com.myme.mywarehome.domains.mrp.application.port.in.MrpBomTreeTraversalUseCase;
import com.myme.mywarehome.domains.mrp.application.port.in.MrpBomTreeUseCase;
import com.myme.mywarehome.domains.mrp.application.port.in.MrpOutputUseCase;
import com.myme.mywarehome.domains.mrp.application.port.in.command.MrpInputCommand;
import com.myme.mywarehome.domains.mrp.application.port.out.GetInventoryRecordPort;
import com.myme.mywarehome.domains.mrp.application.service.dto.MrpCalculateResultDto;
import com.myme.mywarehome.domains.mrp.application.service.dto.MrpContextDto;
import com.myme.mywarehome.domains.mrp.application.service.dto.UnifiedBomDataDto;
import com.myme.mywarehome.domains.product.application.domain.Product;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class MrpOperationServiceTest {

    @Mock
    private CreateInventoryRecordUseCase createInventoryRecordUseCase;

    @Mock
    private GetInventoryRecordPort getInventoryRecordPort;

    @Mock
    private MrpBomTreeUseCase mrpBomTreeUseCase;

    @Mock
    private MrpBomTreeTraversalUseCase mrpBomTreeTraversalUseCase;

    @Mock
    private MrpOutputUseCase mrpOutputUseCase;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private MrpOperationService mrpOperationService;
    private Product product;
    private InventoryRecord inventoryRecord;
    private InventoryRecordItem inventoryRecordItem;
    private LocalDate dueDate;
    private static final String LOCK_KEY = "mrp:operation:lock";

    @BeforeEach
    void setUp() {
        mrpOperationService = new MrpOperationService(
                createInventoryRecordUseCase,
                getInventoryRecordPort,
                mrpBomTreeUseCase,
                mrpBomTreeTraversalUseCase,
                mrpOutputUseCase,
                redisTemplate
        );

        dueDate = LocalDate.now().plusDays(30);

        product = Product.builder()
                .productId(1L)
                .productNumber("TEST-001")
                .productName("Test Product")
                .build();

        inventoryRecord = InventoryRecord.builder()
                .stockStatusAt(LocalDateTime.now())
                .build();

        inventoryRecordItem = InventoryRecordItem.builder()
                .inventoryRecord(inventoryRecord)
                .product(product)
                .stockCount(100L)
                .build();
    }

    @Test
    @DisplayName("MRP 계산을 정상적으로 수행한다")
    void run_withValidInput_shouldCompleteSuccessfully() {
        // given
        Map<String, Integer> engineCountMap = Map.of("kappa", 1);
        MrpInputCommand command = new MrpInputCommand(engineCountMap, dueDate);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(eq(LOCK_KEY), anyString(), any(Duration.class)))
                .thenReturn(true);

        when(getInventoryRecordPort.findRecentInventoryRecord())
                .thenReturn(List.of(inventoryRecordItem));

        UnifiedBomDataDto unifiedBomData = new UnifiedBomDataDto(
                product,
                List.of(),
                Map.of()
        );
        when(mrpBomTreeUseCase.createUnifiedBomTree(command))
                .thenReturn(unifiedBomData);

        MrpCalculateResultDto calculateResult = new MrpCalculateResultDto(
                0,
                List.of(),
                List.of(),
                List.of(),
                0
        );
        when(mrpBomTreeTraversalUseCase.traverse(eq(command), eq(unifiedBomData), any(MrpContextDto.class)))
                .thenReturn(calculateResult);

        doNothing().when(mrpOutputUseCase).saveResults(command, calculateResult);

        // when
        mrpOperationService.run(command);

        // then
        verify(createInventoryRecordUseCase).createInventoryRecord();
        verify(getInventoryRecordPort).findRecentInventoryRecord();
        verify(mrpBomTreeUseCase).createUnifiedBomTree(command);
        verify(mrpBomTreeTraversalUseCase).traverse(eq(command), eq(unifiedBomData), any(MrpContextDto.class));
        verify(mrpOutputUseCase).saveResults(command, calculateResult);
        verify(redisTemplate).delete(LOCK_KEY);
    }

    @Test
    @DisplayName("이미 MRP 계산이 진행 중일 경우 예외가 발생한다")
    void run_whenLockExists_shouldThrowException() {
        // given
        Map<String, Integer> engineCountMap = Map.of("kappa", 1);
        MrpInputCommand command = new MrpInputCommand(engineCountMap, dueDate);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(eq(LOCK_KEY), anyString(), any(Duration.class)))
                .thenReturn(false);

        // when, then
        assertThatThrownBy(() -> mrpOperationService.run(command))
                .isInstanceOf(MrpCalculationInProgressException.class);
    }
}