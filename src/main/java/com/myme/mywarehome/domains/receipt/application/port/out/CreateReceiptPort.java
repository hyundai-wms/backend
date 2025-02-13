package com.myme.mywarehome.domains.receipt.application.port.out;

import com.myme.mywarehome.domains.receipt.application.domain.Receipt;

public interface CreateReceiptPort {
    Receipt create(Receipt receipt);
}
