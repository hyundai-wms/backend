package com.myme.mywarehome.domains.receipt.application.port.in;

import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.port.in.command.GetAllReceiptPlanCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetAllReceiptPlanUseCase {
    Page<ReceiptPlan> getAllReceiptPlan(GetAllReceiptPlanCommand command, Pageable pageable);
}
