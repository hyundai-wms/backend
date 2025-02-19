package com.myme.mywarehome.domains.mrp.application.domain;

import com.myme.mywarehome.infrastructure.common.jpa.BaseTimeEntity;
import com.myme.mywarehome.infrastructure.util.helper.StringHelper.CodeGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "mrp_outputs")
public class MrpOutput extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mrpOutputId;

    private String mrpOutputCode;

    private LocalDate createdDate;

    private Integer kappaCount;

    private Integer gammaCount;

    private Integer nuCount;

    private Integer thetaCount;

    private LocalDate dueDate;

    private LocalDate orderedDate;

    private Boolean canOrder;

    private Boolean isOrdered;

    @OneToMany(mappedBy = "mrpOutput")
    private List<PurchaseOrderReport> purchaseOrderReportList;

    private String purchaseOrderReportLink;

    @OneToMany(mappedBy = "mrpOutput")
    private List<ProductionPlanningReport> productionPlanningReportList;

    private String productionPlanningReportLink;

    @OneToMany(mappedBy = "mrpOutput")
    private List<MrpExceptionReport> mrpExceptionReportList;

    private String mrpExceptionReportLink;

    @Builder
    public MrpOutput(Long mrpOutputId,
            String mrpOutputCode,
            LocalDate createdDate,
            LocalDate dueDate,
            LocalDate orderedDate,
            Boolean canOrder,
            Boolean isOrdered,
            Integer kappaCount,
            Integer gammaCount,
            Integer nuCount,
            Integer thetaCount
    ) {
        this.mrpOutputId = mrpOutputId;
        this.mrpOutputCode = mrpOutputCode;
        this.createdDate = createdDate;
        this.dueDate = dueDate;
        this.orderedDate = orderedDate;
        this.canOrder = canOrder;
        this.isOrdered = isOrdered;
        this.kappaCount = kappaCount;
        this.gammaCount = gammaCount;
        this.nuCount = nuCount;
        this.thetaCount = thetaCount;
    }

    // 새로운 코드 부여
    @PostPersist
    private void generateMrpOutputCode() {
        this.mrpOutputCode = CodeGenerator.generateMrpOutputCode(mrpOutputId);
    }

    public void assignWithPurchaseOrderReports(List<PurchaseOrderReport> reports) {
        this.purchaseOrderReportList = reports;
        reports.forEach(report -> report.connectWithMrpOutput(this));
    }

    public void assignWithProductionPlanningReports(List<ProductionPlanningReport> reports) {
        this.productionPlanningReportList = reports;
        reports.forEach(report -> report.connectWithMrpOutput(this));
    }

    public void assignWithMrpExceptionReports(List<MrpExceptionReport> reports) {
        this.mrpExceptionReportList = reports;
        reports.forEach(report -> report.connectWithMrpOutput(this));
    }

    // 링크 업데이트
    public void addPurchaseOrderReportLink(String purchaseOrderReportLink) {
        this.purchaseOrderReportLink = purchaseOrderReportLink;
    }

    public void addProductionPlanningReportLink(String productionPlanningReportLink) {
        this.productionPlanningReportLink = productionPlanningReportLink;
    }

    public void addMrpExceptionReportLink(String mrpExceptionReportLink) {
        this.mrpExceptionReportLink = mrpExceptionReportLink;
    }

    // 생산/발주 지시 성공 시
    public void orderSuccess() {
        this.isOrdered = true;
        this.orderedDate = LocalDate.now();
    }

}
