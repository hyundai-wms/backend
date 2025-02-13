package com.myme.mywarehome.domains.issue.adapter.in.web.response;

import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import java.util.List;
import org.springframework.data.domain.Page;

public record GetAllIssuePlanResponse(
        List<IssuePlanResponse> content,
        Integer pageNumber,
        Integer pageSize,
        Long totalElements,
        Integer totalPages,
        Boolean isFirst,
        Boolean isLast
) {
    public static GetAllIssuePlanResponse from(Page<IssuePlan> issuePlanList) {
        return new GetAllIssuePlanResponse(
                issuePlanList.getContent().stream()
                                .map(IssuePlanResponse::of).toList(),
                issuePlanList.getNumber(),
                issuePlanList.getSize(),
                issuePlanList.getTotalElements(),
                issuePlanList.getTotalPages(),
                issuePlanList.isFirst(),
                issuePlanList.isLast()

        );
    }

}
