package com.myme.mywarehome.infrastructure.config.persistence.init;

import com.myme.mywarehome.domains.company.adapter.out.persistence.CompanyJpaRepository;
import com.myme.mywarehome.domains.company.application.domain.Company;
import com.myme.mywarehome.domains.issue.adapter.out.persistence.IssuePlanJpaRepository;
import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.product.adapter.out.persistence.ProductJpaRepository;
import com.myme.mywarehome.domains.product.application.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("dev")
public class InitDataForDevConfig implements CommandLineRunner {
    private final CompanyJpaRepository companyJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final IssuePlanJpaRepository issuePlanJpaRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // 납품 업체(Company) 20개 생성
        List<Company> companies = new ArrayList<>();
        Company myCompany = Company.builder()
                .companyCode("CZ" + String.format("%08d", 1))
                .companyName("자체생산")
                .companyFax("02-1234-" + String.format("%04d", 1))
                .companyPhone("010-1234-" + String.format("%04d", 1))
                .companyEmail("mywarehome" + "@test.com")
                .isVendor(false)
                .build();
        companies.add(myCompany);
        for (int i = 2; i <= 20; i++) {
            Company vendor = Company.builder()
                    .companyCode("CZ" + String.format("%08d", i))
                    .companyName("테스트업체" + (i - 1))
                    .companyFax("02-1234-" + String.format("%04d", i))
                    .companyPhone("010-1234-" + String.format("%04d", i))
                    .companyEmail("company" + (i - 1) + "@test.com")
                    .isVendor(true)
                    .build();
            companies.add(vendor);
        }
        companyJpaRepository.saveAll(companies);

        // 물품(Product) 20개 생성
        List<Product> products = new ArrayList<>();

        String[] applicableEngineType = {
                "Kappa 엔진",
                "Gamma 엔진",
                "Nu 엔진",
                "Theta 엔진"
        };

        for (int i = 1; i <= 20; i++) {
            Product product = Product.builder()
                    .productNumber("101" + String.format("%02d", i) + "-03P00")
                    .productName("테스트물품" + i)
                    .eachCount(i * 10)
                    .safeItemCount(i * 5)
                    .leadTime(i + 5)
                    .applicableEngine(applicableEngineType[i % 4])
                    .company(companies.get(i - 1))
                    .build();

            products.add(product);
        }
        productJpaRepository.saveAll(products);

        // 출고 예정(IssuePlan) 20개 생성
        List<IssuePlan> issuePlans = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 1; i <= 20; i++) {
            IssuePlan issuePlan = IssuePlan.builder()
                    .issuePlanCode("IP" + String.format("%08d", i))
                    .issuePlanDate(now.plusDays(i).format(formatter))
                    .product(products.get(i - 1))
                    .build();
            issuePlans.add(issuePlan);
        }
        issuePlanJpaRepository.saveAll(issuePlans);
    }
}