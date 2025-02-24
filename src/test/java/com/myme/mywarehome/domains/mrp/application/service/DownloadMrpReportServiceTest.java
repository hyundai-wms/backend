package com.myme.mywarehome.domains.mrp.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.myme.mywarehome.domains.mrp.application.port.out.DownloadMrpReportFilePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

@ExtendWith(MockitoExtension.class)
class DownloadMrpReportServiceTest {

    @Mock
    private DownloadMrpReportFilePort downloadMrpReportFilePort;

    private DownloadMrpReportService downloadMrpReportService;

    @BeforeEach
    void setUp() {
        downloadMrpReportService = new DownloadMrpReportService(downloadMrpReportFilePort);
    }

    @Test
    @DisplayName("구매 보고서 다운로드 시 올바른 S3 키로 파일을 요청한다")
    void downloadReport_whenPurchaseType_shouldGenerateCorrectS3Key() {
        // given
        String mrpOutputCode = "MRP001";
        String reportType = "purchase";
        String expectedKey = "mrp-reports/MRP001/purchase_order_report.xlsx";
        Resource mockResource = new ByteArrayResource(new byte[0]);
        when(downloadMrpReportFilePort.downloadFile(expectedKey)).thenReturn(mockResource);

        // when
        Resource result = downloadMrpReportService.downloadReport(mrpOutputCode, reportType);

        // then
        verify(downloadMrpReportFilePort).downloadFile(expectedKey);
        assertThat(result).isEqualTo(mockResource);
    }

    @Test
    @DisplayName("생산 보고서 다운로드 시 올바른 S3 키로 파일을 요청한다")
    void downloadReport_whenProductionType_shouldGenerateCorrectS3Key() {
        // given
        String mrpOutputCode = "MRP001";
        String reportType = "production";
        String expectedKey = "mrp-reports/MRP001/production_planning_report.xlsx";
        Resource mockResource = new ByteArrayResource(new byte[0]);
        when(downloadMrpReportFilePort.downloadFile(expectedKey)).thenReturn(mockResource);

        // when
        Resource result = downloadMrpReportService.downloadReport(mrpOutputCode, reportType);

        // then
        verify(downloadMrpReportFilePort).downloadFile(expectedKey);
        assertThat(result).isEqualTo(mockResource);
    }

    @Test
    @DisplayName("예외 보고서 다운로드 시 올바른 S3 키로 파일을 요청한다")
    void downloadReport_whenExceptionType_shouldGenerateCorrectS3Key() {
        // given
        String mrpOutputCode = "MRP001";
        String reportType = "exception";
        String expectedKey = "mrp-reports/MRP001/mrp_exception_report.xlsx";
        Resource mockResource = new ByteArrayResource(new byte[0]);
        when(downloadMrpReportFilePort.downloadFile(expectedKey)).thenReturn(mockResource);

        // when
        Resource result = downloadMrpReportService.downloadReport(mrpOutputCode, reportType);

        // then
        verify(downloadMrpReportFilePort).downloadFile(expectedKey);
        assertThat(result).isEqualTo(mockResource);
    }

    @Test
    @DisplayName("잘못된 보고서 타입으로 요청 시 예외가 발생한다")
    void downloadReport_whenInvalidReportType_shouldThrowException() {
        // given
        String mrpOutputCode = "MRP001";
        String invalidReportType = "invalid";

        // when, then
        assertThatThrownBy(() ->
                downloadMrpReportService.downloadReport(mrpOutputCode, invalidReportType)
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid report type");
    }

    @Test
    @DisplayName("구매 보고서의 파일명을 정상적으로 반환한다")
    void getFileName_whenPurchaseType_shouldReturnCorrectFileName() {
        // when
        String fileName = downloadMrpReportService.getFileName("purchase");

        // then
        assertThat(fileName).isEqualTo("purchase_order_report.xlsx");
    }

    @Test
    @DisplayName("생산 보고서의 파일명을 정상적으로 반환한다")
    void getFileName_whenProductionType_shouldReturnCorrectFileName() {
        // when
        String fileName = downloadMrpReportService.getFileName("production");

        // then
        assertThat(fileName).isEqualTo("production_planning_report.xlsx");
    }

    @Test
    @DisplayName("예외 보고서의 파일명을 정상적으로 반환한다")
    void getFileName_whenExceptionType_shouldReturnCorrectFileName() {
        // when
        String fileName = downloadMrpReportService.getFileName("exception");

        // then
        assertThat(fileName).isEqualTo("mrp_exception_report.xlsx");
    }

    @Test
    @DisplayName("잘못된 보고서 타입으로 파일명 요청 시 예외가 발생한다")
    void getFileName_whenInvalidReportType_shouldThrowException() {
        // when, then
        assertThatThrownBy(() ->
                downloadMrpReportService.getFileName("invalid")
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid report type");
    }
}