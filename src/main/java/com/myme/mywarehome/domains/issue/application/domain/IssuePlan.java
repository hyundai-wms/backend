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

    // 연결된 Product 설정
    public void connectWithProduct(Product product) {
        this.product = product;
    }

    // todo: 출고 update itemcount 수정하기 + companycode 빼기
    // issueplan 정보 수정
    public void changeIssuePlanItemCount(Integer IssuePlanItemCount) {
        if(issuePlanItemCount > 0) {
            this.issuePlanItemCount = issuePlanItemCount;
        }
    }

    // 입고 예정일 수정
    public void changeIssuePlanDate(LocalDate issuePlanDate) {
        this.issuePlanDate = issuePlanDate;
    }

}
