package com.myme.mywarehome.domains.receipt.application.service;

import com.myme.mywarehome.domains.receipt.application.domain.Receipt;
import com.myme.mywarehome.domains.receipt.application.port.in.GetAllReceiptUseCase;
import com.myme.mywarehome.domains.receipt.application.port.in.command.GetAllReceiptCommand;
import com.myme.mywarehome.domains.receipt.application.port.out.GetReceiptPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllReceiptService implements GetAllReceiptUseCase {
    private final GetReceiptPort getReceiptPort;

    @Override
    public Page<Receipt> getAllReceipt(GetAllReceiptCommand command, Pageable pageable) {
        return getReceiptPort.findAllReceipts(command, pageable);
    }
}
