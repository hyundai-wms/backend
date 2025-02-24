package com.myme.mywarehome.domains.statistic.application.service;

import com.myme.mywarehome.domains.statistic.adapter.in.web.response.GetMrpStatisticResponse;
import com.myme.mywarehome.domains.statistic.application.port.out.GetMrpStatisticPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GetMrpStatisticServiceTest {

    @Mock
    private GetMrpStatisticPort getMrpStatisticPort;

    private GetMrpStatisticService getMrpStatisticService;

    @BeforeEach
    void setUp() {
        getMrpStatisticService = new GetMrpStatisticService(getMrpStatisticPort);
    }

    @Test
    @DisplayName("MRP 통계 데이터를 조회한다")
    void getMrpStatistic_returnsStatisticData() {
        // given
        List<GetMrpStatisticResponse.CompanyReturnCount> returnCounts = List.of(
                new GetMrpStatisticResponse.CompanyReturnCount("회사A", 50),
                new GetMrpStatisticResponse.CompanyReturnCount("회사B", 40),
                new GetMrpStatisticResponse.CompanyReturnCount("회사C", 30),
                new GetMrpStatisticResponse.CompanyReturnCount("회사D", 20),
                new GetMrpStatisticResponse.CompanyReturnCount("회사E", 10)
        );

        Integer totalReturnCount = 150;

        List<GetMrpStatisticResponse.CompanyProductCount> productCounts = List.of(
                new GetMrpStatisticResponse.CompanyProductCount("회사F", 100),
                new GetMrpStatisticResponse.CompanyProductCount("회사G", 80),
                new GetMrpStatisticResponse.CompanyProductCount("회사H", 60),
                new GetMrpStatisticResponse.CompanyProductCount("회사I", 40),
                new GetMrpStatisticResponse.CompanyProductCount("회사J", 20)
        );

        given(getMrpStatisticPort.getTop5ReturnCounts()).willReturn(returnCounts);
        given(getMrpStatisticPort.getTotalReturnCount()).willReturn(totalReturnCount);
        given(getMrpStatisticPort.getTop5ProductCounts()).willReturn(productCounts);

        // when
        GetMrpStatisticResponse response = getMrpStatisticService.getMrpStatistic();

        // then
        assertThat(response).isNotNull();
        assertThat(response.returnCount())
                .hasSize(5)
                .containsExactlyElementsOf(returnCounts);
        assertThat(response.totalReturnCount()).isEqualTo(totalReturnCount);
        assertThat(response.productCount())
                .hasSize(5)
                .containsExactlyElementsOf(productCounts);

        verify(getMrpStatisticPort).getTop5ReturnCounts();
        verify(getMrpStatisticPort).getTotalReturnCount();
        verify(getMrpStatisticPort).getTop5ProductCounts();
    }

    @Test
    @DisplayName("데이터가 없는 경우 빈 통계 데이터를 반환한다")
    void getMrpStatistic_whenNoData_returnsEmptyStatistics() {
        // given
        List<GetMrpStatisticResponse.CompanyReturnCount> emptyReturnCounts = List.of();
        List<GetMrpStatisticResponse.CompanyProductCount> emptyProductCounts = List.of();
        Integer zeroCount = 0;

        given(getMrpStatisticPort.getTop5ReturnCounts()).willReturn(emptyReturnCounts);
        given(getMrpStatisticPort.getTotalReturnCount()).willReturn(zeroCount);
        given(getMrpStatisticPort.getTop5ProductCounts()).willReturn(emptyProductCounts);

        // when
        GetMrpStatisticResponse response = getMrpStatisticService.getMrpStatistic();

        // then
        assertThat(response).isNotNull();
        assertThat(response.returnCount()).isEmpty();
        assertThat(response.totalReturnCount()).isZero();
        assertThat(response.productCount()).isEmpty();

        verify(getMrpStatisticPort).getTop5ReturnCounts();
        verify(getMrpStatisticPort).getTotalReturnCount();
        verify(getMrpStatisticPort).getTop5ProductCounts();
    }

    @Test
    @DisplayName("일부 데이터만 있는 경우에도 정상적으로 응답한다")
    void getMrpStatistic_withPartialData_returnsPartialStatistics() {
        // given
        List<GetMrpStatisticResponse.CompanyReturnCount> returnCounts = List.of(
                new GetMrpStatisticResponse.CompanyReturnCount("회사A", 50)
        );
        Integer totalReturnCount = 50;
        List<GetMrpStatisticResponse.CompanyProductCount> emptyProductCounts = List.of();

        given(getMrpStatisticPort.getTop5ReturnCounts()).willReturn(returnCounts);
        given(getMrpStatisticPort.getTotalReturnCount()).willReturn(totalReturnCount);
        given(getMrpStatisticPort.getTop5ProductCounts()).willReturn(emptyProductCounts);

        // when
        GetMrpStatisticResponse response = getMrpStatisticService.getMrpStatistic();

        // then
        assertThat(response).isNotNull();
        assertThat(response.returnCount())
                .hasSize(1)
                .containsExactlyElementsOf(returnCounts);
        assertThat(response.totalReturnCount()).isEqualTo(totalReturnCount);
        assertThat(response.productCount()).isEmpty();

        verify(getMrpStatisticPort).getTop5ReturnCounts();
        verify(getMrpStatisticPort).getTotalReturnCount();
        verify(getMrpStatisticPort).getTop5ProductCounts();
    }
}