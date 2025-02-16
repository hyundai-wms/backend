package com.myme.mywarehome.domains.issue.application.port.in.event;


import com.myme.mywarehome.domains.issue.application.domain.Issue;
import com.myme.mywarehome.domains.stock.application.domain.Stock;

import java.util.concurrent.CompletableFuture;

public record IssueStockAssignEvent(
        Issue issue,
        Long stockId,
        CompletableFuture<Stock> future
) {
}
