package com.myme.mywarehome.domains.mrp.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.myme.mywarehome.domains.mrp.application.domain.MrpOutput;
import com.myme.mywarehome.domains.mrp.application.port.in.command.GetAllMrpOutputCommand;
import com.myme.mywarehome.domains.mrp.application.port.out.GetAllMrpOutputPort;
import java.time.LocalDate;
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
class GetAllMrpOutputServiceTest {

    @Mock
    private GetAllMrpOutputPort getAllMrpOutputPort;

    private GetAllMrpOutputService getAllMrpOutputService;

    private List<MrpOutput> mockMrpOutputs;
    private GetAllMrpOutputCommand command;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        getAllMrpOutputService = new GetAllMrpOutputService(getAllMrpOutputPort);

        // Mock 데이터 설정
        mockMrpOutputs = List.of(
                MrpOutput.builder()
                        .mrpOutputCode("MRP001")
                        .createdDate(LocalDate.now())
                        .dueDate(LocalDate.now().plusDays(90))
                        .orderedDate(LocalDate.now())
                        .canOrder(true)
                        .isOrdered(false)
                        .kappaCount(1000)
                        .gammaCount(1000)
                        .nuCount(1000)
                        .thetaCount(1000)
                        .build(),
                MrpOutput.builder()
                        .mrpOutputCode("MRP002")
                        .createdDate(LocalDate.now())
                        .dueDate(LocalDate.now().plusDays(10))
                        .orderedDate(LocalDate.now())
                        .canOrder(false)
                        .isOrdered(false)
                        .kappaCount(1000)
                        .gammaCount(1000)
                        .nuCount(1000)
                        .thetaCount(1000)
                        .build()
        );

        LocalDate now = LocalDate.now();

        command = new GetAllMrpOutputCommand(now, now, false);
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("MRP 출력 목록을 페이지네이션하여 조회한다")
    void findAllMrpOutput_withPagination_shouldReturnPagedResult() {
        // given
        Page<MrpOutput> expectedPage = new PageImpl<>(
                mockMrpOutputs,
                pageable,
                mockMrpOutputs.size()
        );

        when(getAllMrpOutputPort.findAllMrpOutputs(command, pageable))
                .thenReturn(expectedPage);

        // when
        Page<MrpOutput> result = getAllMrpOutputService.findAllMrpOutput(command, pageable);

        // then
        verify(getAllMrpOutputPort).findAllMrpOutputs(command, pageable);

        assertThat(result.getContent())
                .hasSize(2)
                .usingRecursiveComparison()
                .isEqualTo(mockMrpOutputs);

        assertThat(result.getNumber()).isZero();
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotalElements()).isEqualTo(2);

        // 결과 리스트 크기 검증
        assertThat(result.getContent()).hasSize(2);

        // 첫 번째 MRP 출력 검증
        assertThat(result.getContent().get(0))
                .satisfies(output -> {
                    assertThat(output.getMrpOutputCode()).isEqualTo("MRP001");
                    assertThat(output.getCreatedDate()).isEqualTo(LocalDate.now());
                    assertThat(output.getDueDate()).isEqualTo(LocalDate.now().plusDays(90));
                    assertThat(output.getOrderedDate()).isEqualTo(LocalDate.now());
                    assertThat(output.getCanOrder()).isTrue();
                    assertThat(output.getIsOrdered()).isFalse();
                    assertThat(output.getKappaCount()).isEqualTo(1000);
                    assertThat(output.getGammaCount()).isEqualTo(1000);
                    assertThat(output.getNuCount()).isEqualTo(1000);
                    assertThat(output.getThetaCount()).isEqualTo(1000);
                });

        // 두 번째 MRP 출력 검증
        assertThat(result.getContent().get(1))
                .satisfies(output -> {
                    assertThat(output.getMrpOutputCode()).isEqualTo("MRP002");
                    assertThat(output.getCreatedDate()).isEqualTo(LocalDate.now());
                    assertThat(output.getDueDate()).isEqualTo(LocalDate.now().plusDays(10));
                    assertThat(output.getOrderedDate()).isEqualTo(LocalDate.now());
                    assertThat(output.getCanOrder()).isFalse();
                    assertThat(output.getIsOrdered()).isFalse();
                    assertThat(output.getKappaCount()).isEqualTo(1000);
                    assertThat(output.getGammaCount()).isEqualTo(1000);
                    assertThat(output.getNuCount()).isEqualTo(1000);
                    assertThat(output.getThetaCount()).isEqualTo(1000);
                });


    }

    @Test
    @DisplayName("빈 결과를 조회할 경우 빈 페이지를 반환한다")
    void findAllMrpOutput_whenEmpty_shouldReturnEmptyPage() {
        // given
        Page<MrpOutput> emptyPage = new PageImpl<>(
                List.of(),
                pageable,
                0
        );

        when(getAllMrpOutputPort.findAllMrpOutputs(command, pageable))
                .thenReturn(emptyPage);

        // when
        Page<MrpOutput> result = getAllMrpOutputService.findAllMrpOutput(command, pageable);

        // then
        verify(getAllMrpOutputPort).findAllMrpOutputs(command, pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }
}