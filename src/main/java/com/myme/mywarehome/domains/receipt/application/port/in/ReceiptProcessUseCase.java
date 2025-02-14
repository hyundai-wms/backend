package com.myme.mywarehome.domains.receipt.application.port.in;

import com.myme.mywarehome.domains.receipt.application.port.in.command.ReceiptOrReturnProcessCommand;
import com.myme.mywarehome.domains.receipt.application.port.in.command.ReceiptProcessBulkCommand;
import com.myme.mywarehome.domains.stock.application.domain.Stock;

public interface ReceiptProcessUseCase {
    Stock process(String outboundProductId, ReceiptOrReturnProcessCommand command);
    void processBulk(ReceiptProcessBulkCommand command);
}
