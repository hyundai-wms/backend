package com.myme.mywarehome.domains.issue.adapter.in.web;

import com.myme.mywarehome.domains.issue.adapter.in.web.request.CreateIssuePlanRequest;
import com.myme.mywarehome.domains.issue.adapter.in.web.request.UpdateIssuePlanRequest;
import com.myme.mywarehome.domains.issue.adapter.in.web.response.CreateIssuePlanResponse;
import com.myme.mywarehome.domains.issue.adapter.in.web.response.UpdateIssuePlanResponse;
import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.in.CreateIssuePlanUseCase;
import com.myme.mywarehome.domains.issue.application.port.in.UpdateIssuePlanUseCase;
import com.myme.mywarehome.domains.product.adapter.out.persistence.ProductJpaRepository;
import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.product.application.exception.ProductNotFoundException;
import com.myme.mywarehome.domains.product.application.port.out.GetProductPort;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/storages/issues/plans")
@RequiredArgsConstructor
public class IssuePlanController {
    private final CreateIssuePlanUseCase createIssuePlanUseCase;
    private final ProductJpaRepository productJpaRepository;
    private final UpdateIssuePlanUseCase updateIssuePlanUseCase;
    private final GetProductPort getProductPort;

    @PostMapping
    public CommonResponse<CreateIssuePlanResponse> create(@Valid @RequestBody CreateIssuePlanRequest createIssuePlanRequest) {
        Product product = productJpaRepository.findByProductNumber(createIssuePlanRequest.productNumber())
                .orElseThrow(ProductNotFoundException::new);

        return CommonResponse.from(
                CreateIssuePlanResponse.of(
                        createIssuePlanUseCase.create(createIssuePlanRequest.toEntity(product))  // product 전달
                )
        );
    }
    // todo : bulk api는 딱히 필요없으므로 나중에 지우기
    @PostMapping("/bulk")
    public CommonResponse<List<CreateIssuePlanResponse>> createBulk(
            @Valid @RequestBody List<CreateIssuePlanRequest> requests) {

        List<IssuePlan> issuePlanList = requests.stream()
                .map(request -> {
                    Product product = productJpaRepository.findByProductNumber(request.productNumber())
                            .orElseThrow((ProductNotFoundException::new));
                    return request.toEntity(product);
                })
                .collect(Collectors.toList());

        List<IssuePlan> savedPlanList = createIssuePlanUseCase.createBulk(issuePlanList);

        return CommonResponse.from(
                savedPlanList.stream()
                        .map(CreateIssuePlanResponse::of)
                        .collect(Collectors.toList())
        );
    }

    @PutMapping("/{issuePlanId}")
    public CommonResponse<UpdateIssuePlanResponse> update(
            @PathVariable Long issuePlanId,
            @Valid @RequestBody UpdateIssuePlanRequest updateIssuePlanRequest) {

        Product product = getProductPort.findByProductNumber(updateIssuePlanRequest.productNumber())
                .orElseThrow((ProductNotFoundException::new));

        return CommonResponse.from(
                UpdateIssuePlanResponse.of(
                        updateIssuePlanUseCase.update(updateIssuePlanRequest.toEntity(issuePlanId, product))
                )
        );
    }


}
