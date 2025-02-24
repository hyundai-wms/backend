package com.myme.mywarehome.domains.mrp.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.myme.mywarehome.domains.mrp.application.domain.InventoryRecord;
import com.myme.mywarehome.domains.mrp.application.port.in.command.GetAllInventoryRecordCommand;
import com.myme.mywarehome.domains.mrp.application.port.out.GetAllInventoryRecordPort;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class GetAllInventoryRecordServiceTest {

    @Mock
    private GetAllInventoryRecordPort getAllInventoryRecordPort;

    private GetAllInventoryRecordService getAllInventoryRecordService;

    private List<InventoryRecord> mockInventoryRecords;
    private GetAllInventoryRecordCommand command;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        LocalDate now = LocalDate.now();

        getAllInventoryRecordService = new GetAllInventoryRecordService(getAllInventoryRecordPort);

        // Mock 데이터 설정
        mockInventoryRecords = List.of(
                InventoryRecord.builder()
                        .stockStatusAt(LocalDateTime.now().minusDays(1))
                        .build(),
                InventoryRecord.builder()
                        .stockStatusAt(LocalDateTime.now())
                        .build()
        );

        command = new GetAllInventoryRecordCommand(now, now);
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("재고 기록 목록을 페이지네이션하여 조회한다")
    void findAllInventoryRecord_withPagination_shouldReturnPagedResult() {
        // given
        Page<InventoryRecord> expectedPage = new PageImpl<>(
                mockInventoryRecords,
                pageable,
                mockInventoryRecords.size()
        );

        when(getAllInventoryRecordPort.findAllInventoryRecords(command, pageable))
                .thenReturn(expectedPage);

        // when
        Page<InventoryRecord> result = getAllInventoryRecordService
                .findAllInventoryRecord(command, pageable);

        // then
        verify(getAllInventoryRecordPort).findAllInventoryRecords(command, pageable);

        assertThat(result.getContent())
                .hasSize(2)
                .usingRecursiveComparison()
                .isEqualTo(mockInventoryRecords);

        assertThat(result.getNumber()).isZero();
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("빈 결과를 조회할 경우 빈 페이지를 반환한다")
    void findAllInventoryRecord_whenEmpty_shouldReturnEmptyPage() {
        // given
        Page<InventoryRecord> emptyPage = new PageImpl<>(
                List.of(),
                pageable,
                0
        );

        when(getAllInventoryRecordPort.findAllInventoryRecords(command, pageable))
                .thenReturn(emptyPage);

        // when
        Page<InventoryRecord> result = getAllInventoryRecordService
                .findAllInventoryRecord(command, pageable);

        // then
        verify(getAllInventoryRecordPort).findAllInventoryRecords(command, pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }
}