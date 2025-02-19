package com.myme.mywarehome.domains.mrp.application.port.in;

import org.springframework.core.io.Resource;

public interface DownloadMrpReportUseCase {
    Resource downloadReport(String mrpOutputCode, String reportType);
    String getFileName(String reportType);
}
