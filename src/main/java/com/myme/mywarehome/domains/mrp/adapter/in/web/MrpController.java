package com.myme.mywarehome.domains.mrp.adapter.in.web;

import com.myme.mywarehome.domains.mrp.adapter.in.web.request.GetAllMrpOutputRequest;
import com.myme.mywarehome.domains.mrp.adapter.in.web.request.MrpInputRequest;
import com.myme.mywarehome.domains.mrp.adapter.in.web.response.GetAllMrpOutputResponse;
import com.myme.mywarehome.domains.mrp.application.port.in.GetAllMrpOutputUseCase;
import com.myme.mywarehome.domains.mrp.application.port.in.MrpOperationUseCase;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/productions/mrp")
@RequiredArgsConstructor
public class MrpController {
    private final MrpOperationUseCase mrpOperationUseCase;
    private final GetAllMrpOutputUseCase getAllMrpOutputUseCase;

    @PostMapping
    public CommonResponse<Void> run(
            @Valid @RequestBody MrpInputRequest request
    ) {
        mrpOperationUseCase.run(request.toCommand());
        return CommonResponse.empty();
    }

    @GetMapping("/outputs")
    public CommonResponse<GetAllMrpOutputResponse> getAllMrpOutputs(
            @Valid GetAllMrpOutputRequest request,
            @PageableDefault @SortDefault.SortDefaults({
                    @SortDefault(sort = "createdAt", direction = Direction.DESC),
                    @SortDefault(sort = "mrpOutputId", direction = Direction.ASC)
            }) Pageable pageable
    ) {
        return CommonResponse.from(
                GetAllMrpOutputResponse.from(getAllMrpOutputUseCase.findAllMrpOutput(request.toCommand(), pageable))
        );
    }
}
