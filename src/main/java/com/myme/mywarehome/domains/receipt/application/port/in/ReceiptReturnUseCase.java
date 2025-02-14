package com.myme.mywarehome.domains.receipt.application.port.in;

import com.myme.mywarehome.domains.receipt.application.port.in.command.SelectedDateCommand;

public interface ReceiptReturnUseCase {
    void process(String outboundProductId, SelectedDateCommand command);
}
