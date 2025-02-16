package com.myme.mywarehome.domains.stock.application.port.in;

import com.myme.mywarehome.domains.issue.application.domain.Issue;
import com.myme.mywarehome.domains.stock.application.domain.Stock;

public interface IssueAssignWithStockUseCase {
    Stock assignIssue(Issue issue, Long stockId);
}
