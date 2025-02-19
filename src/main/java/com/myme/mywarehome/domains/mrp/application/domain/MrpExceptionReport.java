package com.myme.mywarehome.domains.mrp.application.domain;

import com.myme.mywarehome.infrastructure.common.jpa.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "mrp_exception_reports")
public class MrpExceptionReport extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mrpExceptionReportId;

    private String exceptionType;

    private String exceptionMessage;

    @ManyToOne
    @JoinColumn(name = "mrp_output_id")
    private MrpOutput mrpOutput;

    @Builder
    public MrpExceptionReport(Long mrpExceptionReportId, String exceptionType, String exceptionMessage, MrpOutput mrpOutput) {
        this.mrpExceptionReportId = mrpExceptionReportId;
        this.exceptionType = exceptionType;
        this.exceptionMessage = exceptionMessage;
        this.mrpOutput = mrpOutput;
    }

    public void connectWithMrpOutput(MrpOutput mrpOutput) {
        this.mrpOutput = mrpOutput;
    }
}
