package com.myme.mywarehome.domains.issue.adapter.out.exception;

import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class IssuePlanItemCountExceededException extends BusinessException {
    public IssuePlanItemCountExceededException() {
        super(ErrorCode.ISSUE_PLAN_ITEM_COUNT_EXCEEDED);
    }
}