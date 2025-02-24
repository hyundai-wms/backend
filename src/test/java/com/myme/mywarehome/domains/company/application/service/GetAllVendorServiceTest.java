package com.myme.mywarehome.domains.company.application.service;

import com.myme.mywarehome.domains.company.application.domain.Company;
import com.myme.mywarehome.domains.company.application.port.out.GetAllVendorPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GetAllVendorServiceTest {

    @InjectMocks
    private GetAllVendorService getAllVendorService;

    @Mock
    private GetAllVendorPort getAllVendorPort;

    @Test
    @DisplayName("업체 코드와 이름으로 검색하여 업체 목록을 페이징하여 조회한다")
    void getAllVendors_WithCompanyCodeAndName_ReturnsPagedVendors() {
        // given
        String companyCode = "VENDOR001";
        String companyName = "테스트업체";
        Pageable pageable = PageRequest.of(0, 10);

        Company vendor = Company.builder()
                .companyCode(companyCode)
                .companyName(companyName)
                .build();
        Page<Company> expectedPage = new PageImpl<>(List.of(vendor));

        given(getAllVendorPort.findVendors(eq(companyCode), eq(companyName), any(Pageable.class)))
                .willReturn(expectedPage);

        // when
        Page<Company> actualPage = getAllVendorService.getAllVendors(companyCode, companyName, pageable);

        // then
        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getContent()).hasSize(1);
        assertThat(actualPage.getContent().get(0))
                .extracting("companyCode", "companyName")
                .containsExactly(companyCode, companyName);

        verify(getAllVendorPort).findVendors(companyCode, companyName, pageable);
    }

    @Test
    @DisplayName("검색 조건 없이 모든 업체 목록을 페이징하여 조회한다")
    void getAllVendors_WithoutSearchConditions_ReturnsAllPagedVendors() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Company> expectedPage = new PageImpl<>(List.of(
                Company.builder().companyCode("VENDOR001").companyName("업체1").build(),
                Company.builder().companyCode("VENDOR002").companyName("업체2").build()
        ));

        given(getAllVendorPort.findVendors(eq(null), eq(null), any(Pageable.class)))
                .willReturn(expectedPage);

        // when
        Page<Company> actualPage = getAllVendorService.getAllVendors(null, null, pageable);

        // then
        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getContent()).hasSize(2);
        verify(getAllVendorPort).findVendors(null, null, pageable);
    }
}