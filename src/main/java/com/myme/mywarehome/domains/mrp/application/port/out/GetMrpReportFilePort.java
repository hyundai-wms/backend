package com.myme.mywarehome.domains.mrp.application.port.out;

import java.net.URL;

public interface GetMrpReportFilePort {
    URL generatePresignedUrl(String fileKey);
}
