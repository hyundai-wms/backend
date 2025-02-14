package com.myme.mywarehome.domains.receipt.application.port.in;

import com.myme.mywarehome.domains.receipt.application.port.in.command.ReceiptOrReturnProcessedCommand;
import com.myme.mywarehome.domains.stock.application.domain.Stock;

public interface ReceiptProcessedUseCase {
    Stock process(String outboundProductId, ReceiptOrReturnProcessedCommand command);
}
