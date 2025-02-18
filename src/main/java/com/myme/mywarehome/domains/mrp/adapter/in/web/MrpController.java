package com.myme.mywarehome.domains.mrp.adapter.in.web;

import com.myme.mywarehome.domains.mrp.adapter.in.web.request.MrpInputRequest;
import com.myme.mywarehome.domains.mrp.application.port.in.MrpOperationUseCase;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/productions/mrp")
@RequiredArgsConstructor
public class MrpController {
    private final MrpOperationUseCase mrpOperationUseCase;

    @PostMapping
    public CommonResponse<Void> run(
            @Valid @RequestBody MrpInputRequest request
    ) {
        mrpOperationUseCase.run(request.toCommand());
        return CommonResponse.empty();
    }
}
