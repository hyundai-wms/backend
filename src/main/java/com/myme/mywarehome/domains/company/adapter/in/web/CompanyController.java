package com.myme.mywarehome.domains.company.adapter.in.web;

import com.myme.mywarehome.domains.company.adapter.in.web.request.GetVendorRequest;
import com.myme.mywarehome.domains.company.adapter.in.web.response.GetVendorResponse;
import com.myme.mywarehome.domains.company.application.port.in.GetVendorUseCase;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/productions")
@RequiredArgsConstructor
public class CompanyController {
    private final GetAllVendorUseCase getAllVendorUseCase;
    private final GetVendorUseCase getVendorUseCase;

    @GetMapping("/vendors")
    public CommonResponse<GetAllVendorResponse> getAllVendors(@Valid GetAllVendorRequest getAllVendorRequest) {
        return CommonResponse.from(
                GetAllVendorResponse.of(getAllVendorUseCase.getAllVendors(getAllVendorRequest.companyCode(),
                        getAllVendorRequest.companyName(), getAllVendorRequest.toPageable()))
        );
    }

    @GetMapping("/vendors/{companyId}")
    public CommonResponse<GetVendorResponse> getVendorById(@PathVariable("companyId") Long companyId,
                                                              @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable ) {
        return CommonResponse.from(
                GetVendorResponse.from(getVendorUseCase.getSpecificVendors(companyId, pageable))
        );
    }




}
