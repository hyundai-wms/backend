package com.myme.mywarehome.domains.mrp.adapter.in.web;

import com.myme.mywarehome.domains.mrp.adapter.in.web.request.MrpInputRequest;
import com.myme.mywarehome.domains.mrp.application.port.in.DownloadMrpReportUseCase;
import com.myme.mywarehome.domains.mrp.application.port.in.MrpOperationUseCase;
import com.myme.mywarehome.infrastructure.common.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/v1/productions/mrp")
@RequiredArgsConstructor
public class MrpController {
    private final MrpOperationUseCase mrpOperationUseCase;
    private final DownloadMrpReportUseCase downloadMrpReportUseCase;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    @PostMapping
    public CommonResponse<Void> run(
            @Valid @RequestBody MrpInputRequest request
    ) {
        mrpOperationUseCase.run(request.toCommand());
        return CommonResponse.empty();
    }

    @GetMapping(value = "/{mrpOutputCode}/purchase/download", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<Resource> downloadPurchaseOrderReport(@PathVariable String mrpOutputCode) {
        if (!"prod".equals(activeProfile)) {
            return ResponseEntity.noContent().build();
        }

        Resource fileResource = downloadMrpReportUseCase.downloadReport(mrpOutputCode, "purchase");
        String filename = downloadMrpReportUseCase.getFileName("purchase");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.builder("attachment")
                                .filename(filename, StandardCharsets.UTF_8)
                                .build().toString())
                .body(fileResource);
    }

    @GetMapping(value = "/{mrpOutputCode}/production/download", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<Resource> downloadProductionOrderReport(@PathVariable String mrpOutputCode) {
        if (!"prod".equals(activeProfile)) {
            return ResponseEntity.noContent().build();
        }

        Resource fileResource = downloadMrpReportUseCase.downloadReport(mrpOutputCode, "production");
        String filename = downloadMrpReportUseCase.getFileName("production");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.builder("attachment")
                                .filename(filename, StandardCharsets.UTF_8)
                                .build().toString())
                .body(fileResource);
    }

    @GetMapping(value = "/{mrpOutputCode}/exception/download", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<Resource> downloadExceptionOrderReport(@PathVariable String mrpOutputCode) {
        if (!"prod".equals(activeProfile)) {
            return ResponseEntity.noContent().build();
        }

        Resource fileResource = downloadMrpReportUseCase.downloadReport(mrpOutputCode, "exception");
        String filename = downloadMrpReportUseCase.getFileName("exception");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.builder("attachment")
                                .filename(filename, StandardCharsets.UTF_8)
                                .build().toString())
                .body(fileResource);
    }
}
