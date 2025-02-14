package com.myme.mywarehome.domains.issue.adapter.in.web;

import com.myme.mywarehome.domains.issue.adapter.in.web.request.GetAllIssueRequest;
import com.myme.mywarehome.domains.issue.adapter.in.web.request.IssueProcessRequest;
import com.myme.mywarehome.domains.issue.adapter.in.web.response.GetAllIssueResponse;
import com.myme.mywarehome.domains.issue.adapter.in.web.response.IssueProcessResponse;
import com.myme.mywarehome.domains.issue.application.port.in.GetAllIssueUseCase;
import com.myme.mywarehome.domains.issue.application.port.in.IssueProcessUseCase;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/storages/issues")
@RequiredArgsConstructor
public class IssueController {
    private final IssueProcessUseCase issueProcessUseCase;
    private final GetAllIssueUseCase getAllIssueUseCase;


    @PostMapping("/{itemId}/items")
    public CommonResponse<IssueProcessResponse> issueProcess(
            @PathVariable("itemId") Long stockId,
            @Valid @RequestBody IssueProcessRequest issueProcessRequest
    ) {
        return CommonResponse.from(
                IssueProcessResponse.from(
                        issueProcessUseCase.process(stockId,issueProcessRequest.toCommand())
                )
        );
    }

    @GetMapping
    public CommonResponse<GetAllIssueResponse> getAllIssues(
            @Valid GetAllIssueRequest getAllIssueRequest,
            @PageableDefault @SortDefault.SortDefaults({
                    @SortDefault(sort = "issueDate", direction = Sort.Direction.DESC),
                    @SortDefault(sort = "issueId", direction = Sort.Direction.ASC)
            }) Pageable pageable
    ) {
        return CommonResponse.from(
                GetAllIssueResponse.from(
                        getAllIssueUseCase.getAllIssue(getAllIssueRequest.toCommand(),pageable)
                )
        );

    }
}
