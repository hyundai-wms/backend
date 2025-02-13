package com.myme.mywarehome.domains.receipt.application.port.in;

import com.myme.mywarehome.domains.receipt.application.port.in.command.ReceiptProcessedCommand;
import com.myme.mywarehome.domains.stock.application.domain.Stock;

public interface ReceiptProcessedUseCase {
    Stock process(String outboundProductId, ReceiptProcessedCommand command);
}
