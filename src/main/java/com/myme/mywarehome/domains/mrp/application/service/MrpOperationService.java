package com.myme.mywarehome.domains.mrp.application.service;

import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecordItem;
import com.myme.mywarehome.domains.mrp.application.exception.MrpCalculationInProgressException;
import com.myme.mywarehome.domains.mrp.application.port.in.CreateInventoryRecordUseCase;
import com.myme.mywarehome.domains.mrp.application.port.in.MrpBomTreeTraversalUseCase;
import com.myme.mywarehome.domains.mrp.application.port.in.MrpBomTreeUseCase;
import com.myme.mywarehome.domains.mrp.application.port.in.MrpOperationUseCase;
import com.myme.mywarehome.domains.mrp.application.port.in.MrpOutputUseCase;
import com.myme.mywarehome.domains.mrp.application.port.in.command.MrpInputCommand;
import com.myme.mywarehome.domains.mrp.application.port.out.GetInventoryRecordPort;
import com.myme.mywarehome.domains.mrp.application.service.dto.MrpCalculateResultDto;
import com.myme.mywarehome.domains.mrp.application.service.dto.MrpContextDto;
import com.myme.mywarehome.domains.mrp.application.service.dto.UnifiedBomDataDto;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MrpOperationService implements MrpOperationUseCase {
    private final CreateInventoryRecordUseCase createInventoryRecordUseCase;
    private final GetInventoryRecordPort getInventoryRecordPort;
    private final MrpBomTreeUseCase mrpBomTreeUseCase;
    private final MrpBomTreeTraversalUseCase mrpBomTreeTraversalUseCase;
    private final MrpOutputUseCase mrpOutputUseCase;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String LOCK_KEY = "mrp:operation:lock";
    private static final long TIMEOUT_SECONDS = 300;

    @Override
    @Transactional
    public void run(MrpInputCommand command) {
        boolean locked = false;

        try {
            locked = Boolean.TRUE.equals(redisTemplate.opsForValue()
                    .setIfAbsent(LOCK_KEY, "LOCKED", Duration.ofSeconds(TIMEOUT_SECONDS)));

            if (!locked) {
                throw new MrpCalculationInProgressException();
            }

            // 1. 재고 기록 생성/조회
            createInventoryRecordUseCase.createInventoryRecord();
            List<InventoryRecordItem> inventoryRecordItemList = getInventoryRecordPort.findRecentInventoryRecord();

            // mrp context 객체 생성 (노드에서 다음 노드로 데이터 전달하기 위함)
            MrpContextDto context = MrpContextDto.builder()
                    .inventoryRecord(inventoryRecordItemList.stream()
                            .collect(Collectors.toMap(
                                    item -> item.getProduct().getProductId(),
                                    item -> item
                            )))
                    .computedDate(command.dueDate().minusDays(1)) // 납기일 하루 전 날 모든 부품 완성
                    .build();

            // 2. 통합 BOM Tree 생성
            UnifiedBomDataDto unifiedBomData = mrpBomTreeUseCase.createUnifiedBomTree(command);

            // 3. BFS 순회 및 계산
            MrpCalculateResultDto result = mrpBomTreeTraversalUseCase.traverse(command,
                    unifiedBomData, context);

            // 4. 결과 저장
            mrpOutputUseCase.saveResults(command, result);

        } finally {
            if (locked) {
                redisTemplate.delete(LOCK_KEY);
            }
        }
    }

}
