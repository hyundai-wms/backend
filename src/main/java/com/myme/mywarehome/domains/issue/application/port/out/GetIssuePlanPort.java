package com.myme.mywarehome.domains.issue.application.port.out;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.in.command.GetAllIssuePlanCommand;

import java.time.LocalDate;
import java.util.Optional;

import com.myme.mywarehome.domains.issue.application.port.in.result.TodayIssueResult;
import com.myme.mywarehome.domains.receipt.application.port.in.result.TodayReceiptResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GetIssuePlanPort {
    Optional<IssuePlan> getIssuePlanById(Long issuePlanId);
    Page<IssuePlan> findAllIssuePlans (GetAllIssuePlanCommand command, Pageable pageable);
    boolean existsIssuePlanById(Long issuePlanId);
    Page<TodayIssueResult> findTodayIssues(LocalDate today, Pageable pageable);
}
