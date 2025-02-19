package com.myme.mywarehome.domains.issue.adapter.in.web;

import com.myme.mywarehome.domains.issue.adapter.in.web.request.GetAllIssueRequest;
import com.myme.mywarehome.domains.issue.adapter.in.web.request.IssueProcessRequest;
import com.myme.mywarehome.domains.issue.adapter.in.web.response.GetAllIssueResponse;
import com.myme.mywarehome.domains.issue.adapter.in.web.response.IssueProcessResponse;
import com.myme.mywarehome.domains.issue.adapter.in.web.response.TodayIssueResponse;
import com.myme.mywarehome.domains.issue.application.port.in.GetAllIssueUseCase;
import com.myme.mywarehome.domains.issue.application.port.in.GetTodayIssueUseCase;
import com.myme.mywarehome.domains.issue.application.port.in.IssueProcessUseCase;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import com.myme.mywarehome.infrastructure.config.resolver.SelectedDate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.awt.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/v1/storages/issues")
@RequiredArgsConstructor
public class IssueController {
    private final IssueProcessUseCase issueProcessUseCase;
    private final GetAllIssueUseCase getAllIssueUseCase;
    private final GetTodayIssueUseCase getTodayIssueUseCase;


    @PostMapping("/{itemId}/items")
    public CommonResponse<IssueProcessResponse> issueProcess(
            @PathVariable("itemId") Long stockId,
            @SelectedDate LocalDate selectedDate,
            @Valid @RequestBody IssueProcessRequest issueProcessRequest
    ) {
        return CommonResponse.from(
                IssueProcessResponse.from(
                        issueProcessUseCase.process(stockId, issueProcessRequest.toCommand(), selectedDate)
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

    @GetMapping("/today")
    public CommonResponse<TodayIssueResponse> getTodayIssues(
            @SelectedDate LocalDate selectedDate,
            @PageableDefault Pageable pageable
            ) {
        return CommonResponse.from(
                TodayIssueResponse.from(
                        getTodayIssueUseCase.getTodayIssue(selectedDate, pageable)
                )
        );
    }

    @GetMapping(value = "/today/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Object>> streamTodayIssues(
            @SelectedDate LocalDate selectedDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return getTodayIssueUseCase.subscribeTodayIssues(selectedDate, page, size);
    }


}
