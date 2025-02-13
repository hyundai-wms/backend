package com.myme.mywarehome.domains.receipt.adapter.out;

import com.myme.mywarehome.domains.receipt.adapter.out.persistence.OutboundProductJpaRepository;
import com.myme.mywarehome.domains.receipt.application.domain.OutboundProduct;
import com.myme.mywarehome.domains.receipt.application.port.out.CreateOutboundProductPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateOutboundProductAdapter implements CreateOutboundProductPort {
    private final OutboundProductJpaRepository outboundProductJpaRepository;

    @Override
    public OutboundProduct create(OutboundProduct outboundProduct) {
        return outboundProductJpaRepository.save(outboundProduct);
    }
}
