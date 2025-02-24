package com.myme.mywarehome.domains.product.application.service;

import com.myme.mywarehome.domains.product.application.port.in.command.UpdateSafeItemCountCommand;
import com.myme.mywarehome.domains.product.application.port.out.UpdateProductPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UpdateSafeItemCountServiceTest {

    @InjectMocks
    private UpdateSafeItemCountService updateSafeItemCountService;

    @Mock
    private UpdateProductPort updateProductPort;

    @Test
    @DisplayName("여러 제품의 안전 재고량을 일괄 수정한다")
    void updateAllSafeItemCount_WithValidCommands_UpdatesAllProducts() {
        // given
        List<UpdateSafeItemCountCommand> commands = List.of(
                new UpdateSafeItemCountCommand("00000-00P00", 100),
                new UpdateSafeItemCountCommand("00001-00P00", 200),
                new UpdateSafeItemCountCommand("00002-00P00", 300)
        );

        // when
        updateSafeItemCountService.updateAllSafeItemCount(commands);

        // then
        verify(updateProductPort).updateAllSafeItemCount(commands);
    }

    @Test
    @DisplayName("빈 명령 리스트로 안전 재고량 수정을 요청한다")
    void updateAllSafeItemCount_WithEmptyCommands_ProcessesWithoutError() {
        // given
        List<UpdateSafeItemCountCommand> emptyCommands = List.of();

        // when
        updateSafeItemCountService.updateAllSafeItemCount(emptyCommands);

        // then
        verify(updateProductPort).updateAllSafeItemCount(emptyCommands);
    }
}