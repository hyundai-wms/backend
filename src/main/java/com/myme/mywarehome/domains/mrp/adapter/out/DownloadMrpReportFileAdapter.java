package com.myme.mywarehome.domains.mrp.adapter.out;

import com.myme.mywarehome.domains.mrp.application.exception.MrpReportFileCreationException;
import com.myme.mywarehome.domains.mrp.application.port.out.DownloadMrpReportFilePort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

@Component
@RequiredArgsConstructor
public class DownloadMrpReportFileAdapter implements DownloadMrpReportFilePort {
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Override
    public Resource downloadFile(String fileKey) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileKey)
                    .build();

            return new InputStreamResource(s3Client.getObject(getObjectRequest));

        } catch (Exception e) {
            throw new MrpReportFileCreationException();
        }
    }
}
