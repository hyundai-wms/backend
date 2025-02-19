package com.myme.mywarehome.domains.mrp.adapter.out;

import com.myme.mywarehome.domains.mrp.application.domain.MrpExceptionReport;
import com.myme.mywarehome.domains.mrp.application.domain.MrpOutput;
import com.myme.mywarehome.domains.mrp.application.domain.ProductionPlanningReport;
import com.myme.mywarehome.domains.mrp.application.domain.PurchaseOrderReport;
import com.myme.mywarehome.domains.mrp.application.exception.MrpReportFileCreationException;
import com.myme.mywarehome.domains.mrp.application.port.out.CreateMrpReportFilePort;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateMrpReportFileAdapter implements CreateMrpReportFilePort {
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public String createAndUploadPurchaseOrderReport(MrpOutput mrpOutput, List<PurchaseOrderReport> reports) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Purchase Order Report");

            // 헤더 생성
            Row headerRow = sheet.createRow(0);
            String[] headers = {"발주일자", "입고예정일", "품번", "품명", "발주수량", "안전재고"};
            createHeaders(sheet, headers);

            // 데이터 입력
            int rowNum = 1;
            for (PurchaseOrderReport report : reports) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(report.getPurchaseOrderDate().toString());
                row.createCell(1).setCellValue(report.getReceiptPlanDate().toString());
                row.createCell(2).setCellValue(report.getProduct().getProductNumber());
                row.createCell(3).setCellValue(report.getProduct().getProductName());
                row.createCell(4).setCellValue(report.getQuantity());
                row.createCell(5).setCellValue(report.getSafeItemCount());
            }

            autoSizeColumns(sheet, headers.length);

            // 파일 경로 생성
            String key = generateFileKey(mrpOutput.getMrpOutputCode(), "purchase_order_report");

            // S3 업로드
            uploadToS3(workbook, key);

            return baseUrl + "/" + mrpOutput.getMrpOutputCode() + "/purchase";
        } catch (Exception e) {
            log.error("Failed to create purchase order report", e);
            throw new MrpReportFileCreationException();
        }
    }

    @Override
    public String createAndUploadProductionPlanningReport(MrpOutput mrpOutput, List<ProductionPlanningReport> reports) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Production Planning Report");

            String[] headers = {"생산계획일", "출고예정일", "품번", "품명", "생산수량", "안전재고"};
            createHeaders(sheet, headers);

            int rowNum = 1;
            for (ProductionPlanningReport report : reports) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(report.getProductionPlanningDate().toString());
                row.createCell(1).setCellValue(report.getIssuePlanDate().toString());
                row.createCell(2).setCellValue(report.getProduct().getProductNumber());
                row.createCell(3).setCellValue(report.getProduct().getProductName());
                row.createCell(4).setCellValue(report.getQuantity());
                row.createCell(5).setCellValue(report.getSafeItemCount());
            }

            autoSizeColumns(sheet, headers.length);

            String key = generateFileKey(mrpOutput.getMrpOutputCode(), "production_planning_report");
            uploadToS3(workbook, key);

            return baseUrl + "/" + mrpOutput.getMrpOutputCode() + "/production";
        } catch (Exception e) {
            log.error("Failed to create production planning report", e);
            throw new MrpReportFileCreationException();
        }
    }

    @Override
    public String createAndUploadMrpExceptionReport(MrpOutput mrpOutput, List<MrpExceptionReport> reports) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("MRP Exception Report");

            String[] headers = {"예외 유형", "예외 메시지"};
            createHeaders(sheet, headers);

            int rowNum = 1;
            for (MrpExceptionReport report : reports) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(report.getExceptionType());
                row.createCell(1).setCellValue(report.getExceptionMessage());
            }

            autoSizeColumns(sheet, headers.length);

            String key = generateFileKey(mrpOutput.getMrpOutputCode(), "mrp_exception_report");
            uploadToS3(workbook, key);

            return baseUrl + "/" + mrpOutput.getMrpOutputCode() + "/exception";
        } catch (Exception e) {
            log.error("Failed to create MRP exception report", e);
            throw new MrpReportFileCreationException();
        }
    }

    private void createHeaders(XSSFSheet sheet, String[] headers) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
    }

    private void autoSizeColumns(XSSFSheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private String generateFileKey(String mrpOutputCode, String reportType) {
        return String.format("mrp-reports/%s/%s.xlsx", mrpOutputCode, reportType);
    }

    private void uploadToS3(XSSFWorkbook workbook, String key) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.putObject(putObjectRequest,
                RequestBody.fromBytes(outputStream.toByteArray()));
    }
}
