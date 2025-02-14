package com.myme.mywarehome.domains.receipt.application.port.out;

import com.myme.mywarehome.domains.receipt.application.domain.Receipt;
import com.myme.mywarehome.domains.receipt.application.port.in.command.GetAllReceiptCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface GetReceiptPort {
    Page<Receipt> findAllReceipts(GetAllReceiptCommand command, Pageable pageable);
}
