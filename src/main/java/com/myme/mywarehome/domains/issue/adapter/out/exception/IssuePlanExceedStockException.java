package com.myme.mywarehome.domains.issue.adapter.out.exception;

import com.myme.mywarehome.infrastructure.exception.BusinessException;
import com.myme.mywarehome.infrastructure.exception.ErrorCode;

public class IssuePlanExceedStockException extends BusinessException {
  public IssuePlanExceedStockException() {
    super(ErrorCode.ISSUE_PLAN_EXCEED_STOCK);
  }
}
