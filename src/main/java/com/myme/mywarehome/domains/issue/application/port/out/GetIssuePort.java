package com.myme.mywarehome.domains.issue.application.port.out;

public interface GetIssuePort {
    boolean existsByStockId(Long stockId);
    long countProcessedIssueByIssuePlanId(Long issuePlanId);

}
