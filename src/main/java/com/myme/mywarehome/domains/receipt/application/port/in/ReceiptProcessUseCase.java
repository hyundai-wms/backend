package com.myme.mywarehome.domains.receipt.application.port.in;

import com.myme.mywarehome.domains.receipt.application.port.in.command.SelectedDateCommand;
import com.myme.mywarehome.domains.receipt.application.port.in.command.ReceiptProcessBulkCommand;
import com.myme.mywarehome.domains.stock.application.domain.Stock;

public interface ReceiptProcessUseCase {
    Stock process(String outboundProductId, SelectedDateCommand command);
    void processBulk(ReceiptProcessBulkCommand command);
}
