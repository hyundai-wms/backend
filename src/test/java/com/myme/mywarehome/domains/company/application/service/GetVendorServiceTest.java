package com.myme.mywarehome.domains.company.application.service;

import com.myme.mywarehome.domains.company.application.domain.Company;
import com.myme.mywarehome.domains.company.application.port.out.GetVendorPort;
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
class GetVendorServiceTest {

    @InjectMocks
    private GetVendorService getVendorService;

    @Mock
    private GetVendorPort getVendorPort;

    @Test
    @DisplayName("회사 ID로 특정 업체를 조회한다")
    void getSpecificVendors_WithCompanyId_ReturnsVendor() {
        // given
        Long companyId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Company vendor = Company.builder()
                .companyId(companyId)
                .companyCode("VENDOR001")
                .companyName("테스트업체")
                .build();
        Page<Company> expectedPage = new PageImpl<>(List.of(vendor));

        given(getVendorPort.findVendorByCompanyId(eq(companyId), any(Pageable.class)))
                .willReturn(expectedPage);

        // when
        Page<Company> actualPage = getVendorService.getSpecificVendors(companyId, pageable);

        // then
        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getContent()).hasSize(1);
        assertThat(actualPage.getContent().get(0))
                .extracting("companyId", "companyCode", "companyName")
                .containsExactly(companyId, "VENDOR001", "테스트업체");

        verify(getVendorPort).findVendorByCompanyId(companyId, pageable);
    }

    @Test
    @DisplayName("존재하지 않는 회사 ID로 조회시 빈 페이지를 반환한다")
    void getSpecificVendors_WithNonExistentCompanyId_ReturnsEmptyPage() {
        // given
        Long nonExistentCompanyId = 999L;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Company> emptyPage = new PageImpl<>(List.of());

        given(getVendorPort.findVendorByCompanyId(eq(nonExistentCompanyId), any(Pageable.class)))
                .willReturn(emptyPage);

        // when
        Page<Company> actualPage = getVendorService.getSpecificVendors(nonExistentCompanyId, pageable);

        // then
        assertThat(actualPage).isNotNull();
        assertThat(actualPage.getContent()).isEmpty();

        verify(getVendorPort).findVendorByCompanyId(nonExistentCompanyId, pageable);
    }
}