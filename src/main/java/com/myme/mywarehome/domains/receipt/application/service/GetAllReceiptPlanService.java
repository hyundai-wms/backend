package com.myme.mywarehome.domains.receipt.application.service;

import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.port.in.GetAllReceiptPlanUseCase;
import com.myme.mywarehome.domains.receipt.application.port.in.command.GetAllReceiptPlanCommand;
import com.myme.mywarehome.domains.receipt.application.port.out.GetReceiptPlanPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllReceiptPlanService implements GetAllReceiptPlanUseCase {
    private final GetReceiptPlanPort getReceiptPlanPort;

    @Override
    public Page<ReceiptPlan> getAllReceiptPlan(GetAllReceiptPlanCommand command,
            Pageable pageable) {
        return getReceiptPlanPort.findAllReceiptPlans(command, pageable);
    }
}
