package com.myme.mywarehome.domains.companies.adapter.in.web;

import com.myme.mywarehome.domains.companies.adapter.in.web.request.GetVendorRequest;
import com.myme.mywarehome.domains.companies.adapter.in.web.response.GetVendorResponse;
import com.myme.mywarehome.domains.companies.application.port.in.GetVendorUseCase;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/productions")
@RequiredArgsConstructor
public class CompanyController {
    private final GetVendorUseCase getVendorUseCase;

    @GetMapping("/vendors")
    public CommonResponse<GetVendorResponse> getVendors(@Valid GetVendorRequest getVendorRequest) {
        return CommonResponse.from(
                GetVendorResponse.of(getVendorUseCase.getVendors(getVendorRequest.companyCode(),
                        getVendorRequest.companyName(),getVendorRequest.toPageable()))
        );
    }


}
