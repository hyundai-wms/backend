package com.myme.mywarehome.domains.receipt.application.service;

import com.myme.mywarehome.domains.receipt.application.exception.ReceiptPlanNotFoundException;
import com.myme.mywarehome.domains.receipt.application.port.in.DeleteReceiptPlanUseCase;
import com.myme.mywarehome.domains.receipt.application.port.out.DeleteReceiptPlanPort;
import com.myme.mywarehome.domains.receipt.application.port.out.GetReceiptPlanPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteReceiptPlanService implements DeleteReceiptPlanUseCase {
    private final GetReceiptPlanPort getReceiptPlanPort;
    private final DeleteReceiptPlanPort deleteReceiptPlanPort;

    @Override
    public void deleteReceiptPlan(Long receiptPlanId) {
        if(!getReceiptPlanPort.existsReceiptPlanById(receiptPlanId)) {
            throw new ReceiptPlanNotFoundException();
        }

        deleteReceiptPlanPort.delete(receiptPlanId);
    }
}
