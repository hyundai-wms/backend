package com.myme.mywarehome.domains.receipt.application.port.out;

import com.myme.mywarehome.domains.receipt.application.domain.OutboundProduct;

public interface CreateOutboundProductPort {
    OutboundProduct create(OutboundProduct outboundProduct);
}
