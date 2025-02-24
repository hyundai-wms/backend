package com.myme.mywarehome.domains.issue.application.service;

import com.myme.mywarehome.domains.issue.adapter.out.exception.IssuePlanNotFoundException;
import com.myme.mywarehome.domains.issue.application.port.out.DeleteIssuePlanPort;
import com.myme.mywarehome.domains.issue.application.port.out.GetIssuePlanPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteIssuePlanServiceTest {

    @InjectMocks
    private DeleteIssuePlanService deleteIssuePlanService;

    @Mock
    private GetIssuePlanPort getIssuePlanPort;

    @Mock
    private DeleteIssuePlanPort deleteIssuePlanPort;

    @Test
    @DisplayName("출고 계획을 삭제한다")
    void deleteIssuePlan_WithExistingId_DeletesSuccessfully() {
        // given
        Long issuePlanId = 1L;
        when(getIssuePlanPort.existsIssuePlanById(issuePlanId)).thenReturn(true);

        // when
        deleteIssuePlanService.deleteIssuePlan(issuePlanId);

        // then
        verify(deleteIssuePlanPort, times(1)).delete(issuePlanId);
    }

    @Test
    @DisplayName("존재하지 않는 출고 계획 삭제 시 예외가 발생한다")
    void deleteIssuePlan_WithNonExistingId_ThrowsException() {
        // given
        Long nonExistingId = 999L;
        when(getIssuePlanPort.existsIssuePlanById(nonExistingId)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> deleteIssuePlanService.deleteIssuePlan(nonExistingId))
                .isInstanceOf(IssuePlanNotFoundException.class);

        verify(deleteIssuePlanPort, never()).delete(any());
    }
}