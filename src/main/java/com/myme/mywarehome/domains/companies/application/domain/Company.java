package com.myme.mywarehome.domains.companies.application.domain;

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
public class Company extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="company_id")
    private Long companyId;
    private String companyCode;
    private String companyName;
    private String companyFax;
    private String companyPhone;
    private String companyEmail;
    private Boolean isVendor;

    @OneToMany(mappedBy = "company")
    private List<Product> productList = new ArrayList<>();

    @Builder
    private Company(Long companyId, String companyCode, String companyName, String companyFax, String companyPhone, String companyEmail, Boolean isVendor) {
        this.companyId = companyId;
        this.companyCode = companyCode;
        this.companyName = companyName;
        this.companyFax = companyFax;
        this.companyPhone = companyPhone;
        this.companyEmail = companyEmail;
        this.isVendor = isVendor;
    }


}
