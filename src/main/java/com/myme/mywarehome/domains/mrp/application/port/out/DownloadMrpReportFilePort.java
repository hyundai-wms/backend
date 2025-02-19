package com.myme.mywarehome.domains.mrp.application.port.out;

import org.springframework.core.io.Resource;

public interface DownloadMrpReportFilePort {
    Resource downloadFile(String fileKey);
}
