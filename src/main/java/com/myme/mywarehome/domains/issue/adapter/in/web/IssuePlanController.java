package com.myme.mywarehome.domains.issue.adapter.in.web;

import com.myme.mywarehome.domains.issue.adapter.in.web.request.CreateIssuePlanRequest;
import com.myme.mywarehome.domains.issue.adapter.in.web.response.CreateIssuePlanResponse;
import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.issue.application.port.in.CreateIssuePlanUseCase;
import com.myme.mywarehome.domains.product.adapter.out.persistence.ProductJpaRepository;
import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/issues/plans")
@RequiredArgsConstructor
public class IssuePlanController {
    private final CreateIssuePlanUseCase createIssuePlanUseCase;
    private final ProductJpaRepository productJpaRepository;

    @PostMapping
    public CommonResponse<CreateIssuePlanResponse> create(@Valid @RequestBody CreateIssuePlanRequest createIssuePlanRequest) {
        Product product = productJpaRepository.findByProductNumber(createIssuePlanRequest.productNumber())
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

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
                            .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));
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
}
