package com.myme.mywarehome.domains.company.application.domain;

import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.infrastructure.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name="companies")
@NamedEntityGraph(
        name = "Company.withProducts",
        attributeNodes = @NamedAttributeNode("productList")
)
public class Company extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="company_id")
    private Long companyId;

    @Column(unique = true, nullable = false)
    private String companyCode;

    private String companyName;
    private String companyFax;
    private String companyPhone;
    private String companyEmail;
    private Boolean isVendor;
    private String tier;

    @OneToMany(mappedBy = "company")
    private List<Product> productList = new ArrayList<>();

    @Builder
    private Company(Long companyId, String companyCode, String companyName, String companyFax, String companyPhone, String companyEmail, Boolean isVendor, String tier) {
        this.companyId = companyId;
        this.companyCode = companyCode;
        this.companyName = companyName;
        this.companyFax = companyFax;
        this.companyPhone = companyPhone;
        this.companyEmail = companyEmail;
        this.isVendor = isVendor;
        this.tier = tier;
    }


}
