package com.myme.mywarehome.domains.issue.adapter.in.web;

import com.myme.mywarehome.domains.issue.adapter.in.web.request.CreateIssuePlanRequest;
import com.myme.mywarehome.domains.issue.adapter.in.web.request.GetAllIssuePlanRequest;
import com.myme.mywarehome.domains.issue.adapter.in.web.request.UpdateIssuePlanRequest;
import com.myme.mywarehome.domains.issue.adapter.in.web.response.CreateIssuePlanResponse;
import com.myme.mywarehome.domains.issue.adapter.in.web.response.GetAllIssuePlanResponse;
import com.myme.mywarehome.domains.issue.adapter.in.web.response.IssuePlanResponse;
import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.in.CreateIssuePlanUseCase;
import com.myme.mywarehome.domains.issue.application.port.in.DeleteIssuePlanUseCase;
import com.myme.mywarehome.domains.issue.application.port.in.GetAllIssuePlanUseCase;
import com.myme.mywarehome.domains.issue.application.port.in.UpdateIssuePlanUseCase;
import com.myme.mywarehome.domains.issue.application.port.in.command.IssuePlanCommand;
import com.myme.mywarehome.domains.product.application.port.out.GetProductPort;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/storages/issues/plans")
@RequiredArgsConstructor
public class IssuePlanController {
    private final CreateIssuePlanUseCase createIssuePlanUseCase;
    private final UpdateIssuePlanUseCase updateIssuePlanUseCase;
    private final GetProductPort getProductPort;
    private final DeleteIssuePlanUseCase deleteIssuePlanUseCase;
    private final GetAllIssuePlanUseCase getAllIssuePlanUseCase;

    @PostMapping
    public CommonResponse<CreateIssuePlanResponse> create(@Valid @RequestBody CreateIssuePlanRequest createIssuePlanRequest) {

        return CommonResponse.from(
                CreateIssuePlanResponse.of(
                        createIssuePlanUseCase.create(createIssuePlanRequest.toCommand())
                )
        );
    }
    // todo : bulk api는 딱히 필요없으므로 나중에 지우기
    @PostMapping("/bulk")
    public CommonResponse<List<CreateIssuePlanResponse>> createBulk(
            @Valid @RequestBody List<CreateIssuePlanRequest> requests) {

        List<IssuePlanCommand> commandList = requests.stream()
                .map(CreateIssuePlanRequest::toCommand).toList();

        List<IssuePlan> savedPlanList = createIssuePlanUseCase.createBulk(commandList);

        return CommonResponse.from(
                savedPlanList.stream()
                        .map(CreateIssuePlanResponse::of)
                        .collect(Collectors.toList())
        );
    }

    @PutMapping("/{issuePlanId}")
    public CommonResponse<IssuePlanResponse> update(
            @PathVariable Long issuePlanId,
            @Valid @RequestBody UpdateIssuePlanRequest updateIssuePlanRequest) {

        return CommonResponse.from(
                IssuePlanResponse.of(updateIssuePlanUseCase.update(issuePlanId,
                        updateIssuePlanRequest.toCommand()))
        );
    }

    @DeleteMapping("/{issuePlanId}")
    public CommonResponse<Void> deleteIssuePlan(
            @PathVariable Long issuePlanId
    ) {
        deleteIssuePlanUseCase.deleteIssuePlan(issuePlanId);
        return CommonResponse.empty();
    }

    @GetMapping
    public CommonResponse<GetAllIssuePlanResponse>getAllIssuePlans(
            @Valid GetAllIssuePlanRequest getAllIssuePlanRequest,
            @PageableDefault(sort = "issuePlanDate", direction = Direction.DESC) Pageable pageable
    ) {
        return CommonResponse.from(
                GetAllIssuePlanResponse.from(getAllIssuePlanUseCase.getAllIssuePlan(getAllIssuePlanRequest.toCommand(), pageable))
        );
    }


}
