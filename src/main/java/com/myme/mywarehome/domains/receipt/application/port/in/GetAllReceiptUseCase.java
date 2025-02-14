package com.myme.mywarehome.domains.receipt.application.port.in;

import com.myme.mywarehome.domains.receipt.application.domain.Receipt;
import com.myme.mywarehome.domains.receipt.application.port.in.command.GetAllReceiptCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetAllReceiptUseCase {
    Page<Receipt> getAllReceipt(GetAllReceiptCommand command, Pageable pageable);
}
