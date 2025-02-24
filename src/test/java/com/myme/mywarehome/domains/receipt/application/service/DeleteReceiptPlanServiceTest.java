package com.myme.mywarehome.domains.receipt.application.service;

import com.myme.mywarehome.domains.receipt.application.exception.ReceiptPlanNotFoundException;
import com.myme.mywarehome.domains.receipt.application.port.out.DeleteReceiptPlanPort;
import com.myme.mywarehome.domains.receipt.application.port.out.GetReceiptPlanPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteReceiptPlanServiceTest {

    @InjectMocks
    private DeleteReceiptPlanService deleteReceiptPlanService;

    @Mock
    private GetReceiptPlanPort getReceiptPlanPort;

    @Mock
    private DeleteReceiptPlanPort deleteReceiptPlanPort;

    @Test
    @DisplayName("입고 계획을 삭제한다")
    void deleteReceiptPlan_WithExistingId_DeletesSuccessfully() {
        // given
        Long receiptPlanId = 1L;
        when(getReceiptPlanPort.existsReceiptPlanById(receiptPlanId)).thenReturn(true);

        // when
        deleteReceiptPlanService.deleteReceiptPlan(receiptPlanId);

        // then
        verify(deleteReceiptPlanPort, times(1)).delete(receiptPlanId);
    }

    @Test
    @DisplayName("존재하지 않는 입고 계획 삭제 시 예외가 발생한다")
    void deleteReceiptPlan_WithNonExistingId_ThrowsException() {
        // given
        Long nonExistingId = 999L;
        when(getReceiptPlanPort.existsReceiptPlanById(nonExistingId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> deleteReceiptPlanService.deleteReceiptPlan(nonExistingId))
                .isInstanceOf(ReceiptPlanNotFoundException.class);

        verify(deleteReceiptPlanPort, never()).delete(any());
    }
}