package com.myme.mywarehome.domains.receipt.application.port.in;

import com.myme.mywarehome.domains.receipt.application.domain.Receipt;

public interface ReceiptProcessedUseCase {
    void process(Receipt receipt);
}
