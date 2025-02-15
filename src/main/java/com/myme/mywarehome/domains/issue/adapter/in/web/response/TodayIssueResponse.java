package com.myme.mywarehome.domains.issue.adapter.in.web.response;

import com.myme.mywarehome.domains.issue.application.port.in.result.TodayIssueResult;
import org.springframework.data.domain.Page;

import java.util.List;

public record TodayIssueResponse(
        List<TodayIssueResult> content,
        Integer pageNumber,
        Integer pageSize,
        Long totalElements,
        Integer totalPages,
        Boolean isFirst,
        Boolean isLast
) {
    public static TodayIssueResponse from(Page<TodayIssueResult> content) {
        return new TodayIssueResponse(
                content.getContent(),
                content.getNumber(),
                content.getSize(),
                content.getTotalElements(),
                content.getTotalPages(),
                content.isFirst(),
                content.isLast()
        );
    }
}
