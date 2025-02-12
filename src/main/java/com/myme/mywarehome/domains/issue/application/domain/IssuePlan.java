package com.myme.mywarehome.domains.issue.application.domain;


import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.infrastructure.common.jpa.BaseTimeEntity;
import com.myme.mywarehome.infrastructure.util.helper.StringHelper.CodeGenerator;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name="issue_plans")
public class IssuePlan extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="issue_plan_id")
    private Long issuePlanId;

    @Column(unique = true)
    private String issuePlanCode;
    private Integer issuePlanItemCount;
    private LocalDate issuePlanDate;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id")
    private Product product;

    @Builder
    private IssuePlan(Long issuePlanId,String issuePlanCode, Integer issuePlanItemCount, LocalDate issuePlanDate, Product product) {
        this.issuePlanId = issuePlanId;
        this.issuePlanCode = issuePlanCode;
        this.issuePlanItemCount = issuePlanItemCount;
        this.issuePlanDate = issuePlanDate;
        this.product = product;
    }

    // 새로운 코드 부여
    public void setIssuePlanCode(String issuePlanCode) {
        if(this.issuePlanId != null){
            this.issuePlanCode = CodeGenerator.generateIssuePlanCode(this.issuePlanId);
        }

    }

    // issueplan 정보 수정
    public void updateIssuePlan(Product product, LocalDate issuePlanDate,
            Integer issuePlanItemCount) {
        if (product != null) {
            this.product = product;
        }
        if (issuePlanDate != null) {
            this.issuePlanDate = issuePlanDate;
        }
        if (issuePlanItemCount != null) {
            this.issuePlanItemCount = issuePlanItemCount;
        }
    }

}
