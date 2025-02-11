package com.myme.mywarehome.domains.issue.application.domain;


import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.infrastructure.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Entity
@Getter
@NoArgsConstructor
public class IssuePlan extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="issue_plan_id")
    private Long issuePlanId;
    private String issuePlanCode;
    private String issuePlanDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id")
    private Product product;

    // Todo: Company join 필요
//    @OneToMany(mappedBy = "issuePlan", cascade = CascadeType.ALL)
//    private List<Issue> issueList = new ArrayList<>();

    @Builder
    private IssuePlan(Long issuePlanId,String issuePlanCode,String issuePlanDate, Product product) {
        this.issuePlanId = issuePlanId;
        this.issuePlanCode = issuePlanCode;
        this.issuePlanDate = issuePlanDate;
        this.product = product;
    }

    public void setIssuePlanCode(String issuePlanCode) {
        this.issuePlanCode = issuePlanCode;
    }


}
