package com.myme.mywarehome.domains.mrp.adapter.in.web;

import com.myme.mywarehome.domains.mrp.adapter.in.web.response.GetBomTreeResponse;
import com.myme.mywarehome.domains.mrp.application.port.in.GetBomTreeUseCase;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/productions/bom-trees")
@RequiredArgsConstructor
public class BomTreeController {
    private final GetBomTreeUseCase getBomTreeUseCase;

    @GetMapping("/{applicableEngine}")
    public CommonResponse<GetBomTreeResponse> getBomTreeByEngine(@PathVariable String applicableEngine) {
        return CommonResponse.from(
                GetBomTreeResponse.from(getBomTreeUseCase.getBomTreeByEngine(applicableEngine))
        );
    }


}
