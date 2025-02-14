package com.myme.mywarehome.domains.issue.adapter.in.web;

import com.myme.mywarehome.domains.issue.adapter.in.web.request.IssueProcessRequest;
import com.myme.mywarehome.domains.issue.adapter.in.web.response.IssueProcessResponse;
import com.myme.mywarehome.domains.issue.application.port.in.IssueProcessUseCase;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/storages/issues")
@RequiredArgsConstructor
public class IssueController {
    private final IssueProcessUseCase issueProcessUseCase;


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
}
