package com.myme.mywarehome.domains.receipt.application.port.out;

import com.myme.mywarehome.domains.receipt.application.domain.Return;

public interface CreateReturnPort {
    Return create(Return returnEntity);
}
