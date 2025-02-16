package com.myme.mywarehome.domains.receipt.application.port.in;

import java.time.LocalDate;

public interface ReceiptReturnUseCase {
    void process(String outboundProductId, LocalDate selectedDate);
}
