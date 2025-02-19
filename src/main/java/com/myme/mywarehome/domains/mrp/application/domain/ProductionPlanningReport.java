package com.myme.mywarehome.domains.mrp.application.domain;

import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.infrastructure.common.jpa.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "production_planning_reports")
public class ProductionPlanningReport extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productionPlanningReportId;

    private LocalDate productionPlanningDate;

    private LocalDate receiptPlanDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private Long quantity;

    @ManyToOne
    @JoinColumn(name = "mrp_output_id")
    private MrpOutput mrpOutput;

    private Integer safeItemCount;

    @Builder
    public ProductionPlanningReport(Long productionPlanningReportId, LocalDate productionPlanningDate, LocalDate receiptPlanDate, Product product, Long quantity, MrpOutput mrpOutput, Integer safeItemCount) {
        this.productionPlanningReportId = productionPlanningReportId;
        this.productionPlanningDate = productionPlanningDate;
        this.receiptPlanDate = receiptPlanDate;
        this.product = product;
        this.quantity = quantity;
        this.mrpOutput = mrpOutput;
        this.safeItemCount = safeItemCount;
    }

    public void connectWithMrpOutput(MrpOutput mrpOutput) {
        this.mrpOutput = mrpOutput;
    }
}
