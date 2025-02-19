package com.myme.mywarehome.domains.mrp.application.service;

import com.myme.mywarehome.domains.mrp.application.port.in.DownloadMrpReportUseCase;
import com.myme.mywarehome.domains.mrp.application.port.out.DownloadMrpReportFilePort;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DownloadMrpReportService implements DownloadMrpReportUseCase {
    private final DownloadMrpReportFilePort downloadMrpReportFilePort;

    @Override
    public Resource downloadReport(String mrpOutputCode, String reportType) {
        // todo : report가 있는지 검증

        // S3 key 구성
        String fileKey = generateS3Key(mrpOutputCode, reportType);

        // S3에서 파일 다운로드
        return downloadMrpReportFilePort.downloadFile(fileKey);
    }

    private String generateS3Key(String mrpOutputCode, String reportType) {
        String reportFileName = switch (reportType) {
            case "purchase" -> "purchase_order_report.xlsx";
            case "production" -> "production_planning_report.xlsx";
            case "exception" -> "mrp_exception_report.xlsx";
            default -> throw new IllegalArgumentException("Invalid report type");
        };

        return String.format("mrp-reports/%s/%s", mrpOutputCode, reportFileName);
    }

    @Override
    public String getFileName(String reportType) {
        return switch (reportType) {
            case "purchase" -> "purchase_order_report.xlsx";
            case "production" -> "production_planning_report.xlsx";
            case "exception" -> "mrp_exception_report.xlsx";
            default -> throw new IllegalArgumentException("Invalid report type");
        };
    }
}
