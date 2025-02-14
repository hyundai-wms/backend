package com.myme.mywarehome.domains.receipt.application.domain.service;

import com.myme.mywarehome.domains.receipt.application.domain.OutboundProduct;
import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.exception.DuplicatedOutboundProductException;
import com.myme.mywarehome.domains.receipt.application.exception.ReceiptPlanItemCountCapacityExceededException;
import com.myme.mywarehome.domains.receipt.application.exception.ReceiptPlanNotFoundException;
import com.myme.mywarehome.domains.receipt.application.port.out.CreateOutboundProductPort;
import com.myme.mywarehome.domains.receipt.application.port.out.GetOutboundProductPort;
import com.myme.mywarehome.domains.receipt.application.port.out.GetReceiptPlanPort;
import com.myme.mywarehome.infrastructure.util.helper.StringHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboundProductDomainService {
    private final GetOutboundProductPort getOutboundProductPort;
    private final CreateOutboundProductPort createOutboundProductPort;
    private final GetReceiptPlanPort getReceiptPlanPort;

    public ReceiptPlan validateAndCreateOutboundProduct(String outboundProductId) {
        // 1. 해당 물품이 이미 등록되어있는지 확인
        if(getOutboundProductPort.existsByOutboundProductId(outboundProductId)) {
            throw new DuplicatedOutboundProductException();
        }

        // 2. 파싱하여 입고예정 id 가져오기
        Long receiptPlanId = StringHelper.parseReceiptPlanId(outboundProductId);

        // 3. 물품이 더 이상 처리될 수 있는지 확인(입고 예정 수량만큼 체크)
        ReceiptPlan receiptPlan = getReceiptPlanPort.findReceiptPlanById(receiptPlanId)
                .orElseThrow(ReceiptPlanNotFoundException::new);

        long currentProcessedCount = getOutboundProductPort.countByReceiptPlanId(receiptPlanId);

        if(receiptPlan.getReceiptPlanItemCount() <= currentProcessedCount) {
            throw new ReceiptPlanItemCountCapacityExceededException();
        }

        // 4. OutboundProduct 생성
        OutboundProduct outboundProduct = OutboundProduct.builder()
                .outboundProductId(outboundProductId)
                .receiptPlanId(receiptPlanId)
                .build();

        createOutboundProductPort.create(outboundProduct);

        return receiptPlan;
    }
}
