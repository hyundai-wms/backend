package com.myme.mywarehome.domains.receipt.application.port.in;

import com.myme.mywarehome.domains.receipt.application.port.in.command.ReceiptOrReturnProcessCommand;

public interface ReceiptReturnUseCase {
    void process(String outboundProductId, ReceiptOrReturnProcessCommand command);
}
