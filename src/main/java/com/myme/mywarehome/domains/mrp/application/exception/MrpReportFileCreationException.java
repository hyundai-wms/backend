package com.myme.mywarehome.domains.mrp.application.exception;

import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class MrpReportFileCreationException extends BusinessException {

    public MrpReportFileCreationException() {
        super(ErrorCode.MRP_REPORT_CREATE_FAILED);
    }
}
