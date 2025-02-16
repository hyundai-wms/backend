package com.myme.mywarehome.domains.issue.application.domain;

import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.stock.application.domain.Bin;
import com.myme.mywarehome.domains.stock.application.domain.Stock;
import com.myme.mywarehome.infrastructure.common.jpa.BaseTimeEntity;
import com.myme.mywarehome.infrastructure.util.helper.StringHelper;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name="issues")
public class Issue extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="issue_id")
    private Long issueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_plan_id")
    private IssuePlan issuePlan;

    @OneToOne(mappedBy = "issue")
    private Stock stock;

    @Column(unique = true)
    private String issueCode;
    private LocalDate issueDate;

    @Builder
    private Issue(Long issueId, Product product, IssuePlan issuePlan, Stock stock, String issueCode, LocalDate issueDate) {
        this.issueId = issueId;
        this.product = product;
        this.issuePlan = issuePlan;
        this.stock = stock;
        this.issueCode = issueCode;
        this.issueDate = issueDate;
    }

    @PostPersist
    public void generateReceiptCode() {
        this.issueCode = StringHelper.CodeGenerator.generateReceiptCode(this.issueId);
    }

    // 연결된 Stock 설정
    public void connectWithStock(Stock stock) {
        this.stock = stock;
    }
}
