package com.myme.mywarehome.domains.issue.adapter.in.web.response;

import com.myme.mywarehome.domains.issue.application.domain.Issue;
import org.springframework.data.domain.Page;
import java.util.List;

public record GetAllIssueResponse(
        List<IssueResponse> content,
        Integer pageNumber,
        Integer pageSize,
        Long totalElements,
        Integer totalPages,
        Boolean isFirst,
        Boolean isLast
) {
    public static GetAllIssueResponse from(Page<Issue> issueList) {
        return new GetAllIssueResponse(
                issueList.getContent().stream()
                        .map(IssueResponse::from)
                        .toList(),
                issueList.getNumber(),
                issueList.getSize(),
                issueList.getTotalElements(),
                issueList.getTotalPages(),
                issueList.isFirst(),
                issueList.isLast()
        );
    }
}
