package com.myme.mywarehome.infrastructure.config.persistence.init;

import com.myme.mywarehome.domains.company.adapter.out.persistence.CompanyJpaRepository;
import com.myme.mywarehome.domains.company.application.domain.Company;
import com.myme.mywarehome.domains.issue.adapter.out.persistence.IssuePlanJpaRepository;
import com.myme.mywarehome.domains.issue.application.domain.IssuePlan;
import com.myme.mywarehome.domains.product.adapter.out.persistence.ProductJpaRepository;
import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.stock.adapter.out.persistence.BayJpaRepository;
import com.myme.mywarehome.domains.stock.adapter.out.persistence.BinJpaRepository;
import com.myme.mywarehome.domains.stock.application.domain.Bay;
import com.myme.mywarehome.domains.stock.application.domain.Bin;
import java.util.Random;
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
    private final BinJpaRepository binJpaRepository;
    private final BayJpaRepository bayJpaRepository;

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
        Random random = new Random();

        for (int i = 1; i <= 20; i++) {
            // 1~100 사이의 랜덤한 아이템 수량
            int randomItemCount = random.nextInt(100) + 1;

            // 현재 날짜로부터 1~30일 사이의 랜덤한 날짜
            int randomDays = random.nextInt(30) + 1;
            IssuePlan issuePlan = IssuePlan.builder()
                    .issuePlanCode("IP" + String.format("%08d", i))
                    .issuePlanItemCount(randomItemCount)
                    .issuePlanDate(now.plusDays(i).toLocalDate())
                    .product(products.get(i - 1))
                    .build();
            issuePlans.add(issuePlan);
        }
        issuePlanJpaRepository.saveAll(issuePlans);

        // Bay 50개 생성 (AA01-AA10, AB01-AB10, ..., AE01-AE10)
        List<Bay> bays = new ArrayList<>();
        char firstLetter = 'A';
        char secondLetter = 'A';

        for (int i = 0; i < 5; i++) { // A to E
            for (int j = 1; j <= 10; j++) { // 01 to 10
                String bayNumber = String.format("%c%c%02d", firstLetter, (char)(secondLetter + i), j);
                Bay bay = Bay.builder()
                        .bayNumber(bayNumber)
                        .product(products.get(i % products.size())) // Products를 순환하면서 할당
                        .build();
                bays.add(bay);
            }
        }
        bayJpaRepository.saveAll(bays);

        // Bin 500개 생성 (각 Bay당 10개의 Bin)
        List<Bin> bins = new ArrayList<>();
        for (Bay bay : bays) {
            for (int location = 1; location <= 10; location++) {
                Bin bin = Bin.builder()
                        .bay(bay)
                        .binLocation(location)
                        .build();
                bins.add(bin);
            }
        }
        binJpaRepository.saveAll(bins);
    }
}