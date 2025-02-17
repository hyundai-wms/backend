package com.myme.mywarehome.domains.receipt.application.port.in;

import com.myme.mywarehome.domains.receipt.application.port.in.command.ReceiptProcessBulkCommand;
import com.myme.mywarehome.domains.stock.application.domain.Stock;

import java.time.LocalDate;

public interface ReceiptProcessUseCase {
    Stock process(String outboundProductId, LocalDate selectedDate);
    void processBulk(ReceiptProcessBulkCommand command, LocalDate selectedDate);
}
