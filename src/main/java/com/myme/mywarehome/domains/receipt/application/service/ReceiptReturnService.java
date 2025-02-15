package com.myme.mywarehome.domains.receipt.application.service;

import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.domain.Return;
import com.myme.mywarehome.domains.receipt.application.domain.service.OutboundProductDomainService;
import com.myme.mywarehome.domains.receipt.application.port.in.ReceiptReturnUseCase;
import com.myme.mywarehome.domains.receipt.application.port.out.CreateReturnPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReceiptReturnService implements ReceiptReturnUseCase {
    private final OutboundProductDomainService outboundProductDomainService;
    private final CreateReturnPort createReturnPort;

    @Override
    @Transactional
    public void process(String outboundProductId, LocalDate selectedDate) {
        // 1. OutboundProduct 검증 및 연관 된 ReceiptPlan 가져오기
        ReceiptPlan receiptPlan = outboundProductDomainService.validateAndCreateOutboundProduct(outboundProductId);

        // 2. 반품 기록을 생성
        Return returnEntity = Return.builder()
                .receiptPlan(receiptPlan)
                .returnDate(selectedDate)
                .build();

        createReturnPort.create(returnEntity);
    }
}
