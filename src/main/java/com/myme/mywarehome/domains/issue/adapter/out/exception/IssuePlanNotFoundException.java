package com.myme.mywarehome.domains.issue.adapter.out.exception;
import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class IssuePlanNotFoundException extends BusinessException{
         public IssuePlanNotFoundException() {
            super(ErrorCode.ISSUE_PLAN_NOT_FOUND);
        }
}

