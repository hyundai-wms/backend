package com.myme.mywarehome.infrastructure.config.persistence.init;

import com.myme.mywarehome.domains.company.adapter.out.persistence.CompanyJpaRepository;
import com.myme.mywarehome.domains.company.application.domain.Company;
import com.myme.mywarehome.domains.issue.adapter.out.persistence.IssuePlanJpaRepository;
import com.myme.mywarehome.domains.product.adapter.out.persistence.ProductJpaRepository;
import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.stock.adapter.out.persistence.BayJpaRepository;
import com.myme.mywarehome.domains.stock.adapter.out.persistence.BinJpaRepository;
import com.myme.mywarehome.domains.stock.application.domain.Bay;
import com.myme.mywarehome.domains.stock.application.domain.Bin;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("prod")
public class InitDataForProdConfig implements CommandLineRunner {
    private final CompanyJpaRepository companyJpaRepository;
    private final ProductJpaRepository productJpaRepository;
    private final IssuePlanJpaRepository issuePlanJpaRepository;
    private final BinJpaRepository binJpaRepository;
    private final BayJpaRepository bayJpaRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (companyJpaRepository.count() > 0 || productJpaRepository.count() > 0) {
            return; // Skip if data already exists
        }

        // 1. Company 초기화
        List<Company> companies = new ArrayList<>();

        // Add in-house manufacturing company (ID: 1)
        companies.add(createCompany(1, "자체생산", false, "자체생산"));

        // Add T1 companies
        List<String> t1Companies = Arrays.asList(
                "포스코", "Mahle", "현대제철", "Fel-Pro", "Bosch",
                "Continental", "Denso", "BorgWarner", "Aisin", "Delphi",
                "NGK", "Modine", "Schaeffler"
        );

        int currentId = 2;
        for (String companyName : t1Companies) {
            companies.add(createCompany(currentId++, companyName, true, "T1"));
        }

        // Add T2 companies
        List<String> t2Companies = Arrays.asList(
                "대원산업", "동아튜브", "대원공업", "한국필터",
                "신성씰테크", "우진스프링", "한일고무"
        );

        for (String companyName : t2Companies) {
            companies.add(createCompany(currentId++, companyName, true, "T2"));
        }

        List<Company> savedCompanies = companyJpaRepository.saveAll(companies);

        // 2. Product 초기화
        List<Product> products = new ArrayList<>();
        String[] engineTypes = {"03", "04", "05", "06"};
        String[] engineNames = {"Kappa 엔진", "Gamma 엔진", "Nu 엔진", "Theta 엔진"};

        // Company Map 생성
        var companyMap = savedCompanies.stream()
                .collect(Collectors.toMap(Company::getCompanyName, company -> company));

        // First, create common parts (01 engine type)
        createCommonParts(products, companyMap);

        // Then create engine-specific parts
        for (int i = 0; i < engineTypes.length; i++) {
            initializeEngineProducts(products, engineTypes[i], engineNames[i], companyMap);
        }

       // productJpaRepository.saveAll(products);
        List<Product> savedProducts = productJpaRepository.saveAll(products);

        // 3. Bay 초기화
        //initializeBays(savedProducts);

        List<Bay> savedBays = bayJpaRepository.saveAll(initializeBays(savedProducts));

        // Bin 초기화 추가
        initializeBins(savedBays);

    }

    private Company createCompany(int id, String companyName, boolean isVendor, String tier) {
        return Company.builder()
                .companyCode("CZ" + String.format("%08d", id))
                .companyName(companyName)
                .companyFax("02-1234-" + String.format("%04d", id))
                .companyPhone("010-1234-" + String.format("%04d", id))
                .companyEmail("company" + id + "@test.com")
                .isVendor(isVendor)
                .tier(tier)
                .build();
    }

    private List<Bay> initializeBays(List<Product> products) {
        List<Bay> bays = new ArrayList<>();
        final int TOTAL_BAYS = 3200;
        final int PRODUCTS_COUNT = products.size();

        int baseAllocation = TOTAL_BAYS / PRODUCTS_COUNT;
        int remainingBays = TOTAL_BAYS - (baseAllocation * PRODUCTS_COUNT);

        int totalBaysCreated = 0;

        for (int productIndex = 0; productIndex < products.size(); productIndex++) {
            Product product = products.get(productIndex);
            int baysForThisProduct = baseAllocation;
            if (productIndex < remainingBays) {
                baysForThisProduct++;
            }

            for (int i = 0; i < baysForThisProduct; i++) {
                int currentPosition = totalBaysCreated + i;
                String bayNumber = String.format("%c%c%02d",
                        (char)('A' + (currentPosition / 520)),
                        (char)('A' + ((currentPosition % 520) / 20)),
                        (currentPosition % 20) + 1);

                Bay bay = Bay.builder()
                        .bayNumber(bayNumber)
                        .product(product)
                        .binList(new ArrayList<>())
                        .build();

                bays.add(bay);
            }
            totalBaysCreated += baysForThisProduct;
        }

        if (bays.size() != TOTAL_BAYS) {
            throw new IllegalStateException(
                    String.format("Expected %d bays but created %d", TOTAL_BAYS, bays.size())
            );
        }

        return bays;  // List<Bay> 반환
    }

    private void initializeEngineProducts(List<Product> products, String engineType, String engineName, Map<String, Company> companyMap) {
        // Base engine
        addProduct(products, "10000", engineType, engineName, engineName, companyMap.get("자체생산"), 1, 6);

        // 1. Cylinder Block Module
        addProduct(products, "10100", engineType, engineName, "실린더 블록 모듈", companyMap.get("자체생산"), 1, 6); // 복합공정
        addProduct(products, "10120", engineType, engineName, "실린더 블록", companyMap.get("포스코"), 1, 6); // 복합공정
        addProduct(products, "10130", engineType, engineName, "피스톤 모듈", companyMap.get("Mahle"), 4, 3); // 정밀가공
        addProduct(products, "10140", engineType, engineName, "커넥팅 로드 모듈", companyMap.get("Mahle"), 4, 3); // 정밀가공
        addProduct(products, "10150", engineType, engineName, "크랭크샤프트 모듈", companyMap.get("현대제철"), 1, 6); // 복합공정
        addProduct(products, "10200", engineType, engineName, "실린더 헤드 모듈", companyMap.get("Fel-Pro"), 1, 6); // 복합공정

        // 2. Fuel Supply Module
        Company fuelSupplyVendor = getFuelSupplyVendor(engineType, companyMap);
        addProduct(products, "60000", engineType, engineName, "연료공급 모듈", fuelSupplyVendor, 1, 3); // 정밀가공

        // 3. Intake/Exhaust Module
        addProduct(products, "50000", engineType, engineName, "흡배기 모듈", companyMap.get("자체생산"), 1, 3); // 정밀가공
        Company intakeVendor = getIntakeVendor(engineType, companyMap);
        addProduct(products, "50100", engineType, engineName, "흡기 매니폴드 모듈", intakeVendor, 1, 3); // 정밀가공
        Company exhaustVendor = getExhaustVendor(engineType, companyMap);
        addProduct(products, "50200", engineType, engineName, "배기 매니폴드 모듈", exhaustVendor, 1, 3); // 정밀가공

        // Add turbocharger for Gamma and Theta engines
        if (engineType.equals("04") || engineType.equals("06")) {
            addProduct(products, "50400", engineType, engineName, "터보차저 모듈", companyMap.get("BorgWarner"), 1, 6); // 복합공정
        }

        // Add EGR module
        if (engineType.equals("04") || engineType.equals("06")) {
            // Gamma와 Theta는 각자의 EGR 모듈 사용
            addProduct(products, "50300", engineType, engineName, "EGR 모듈", companyMap.get("BorgWarner"), 1, 3);
        } else if (engineType.equals("03")) {
            // Kappa 처리할 때만 엔진 EGR 모듈 생성
            addProduct(products, "50300", "02", engineName, "EGR 모듈", companyMap.get("BorgWarner"), 1, 3);
        }

        // 4. Ignition Module
        Company ignitionVendor = getIgnitionVendor(engineType, companyMap);
        addProduct(products, "70100", engineType, engineName, "점화 모듈", ignitionVendor, 1, 3);

        // 5. Cooling Module
        addProduct(products, "40000", engineType, engineName, "냉각 모듈", companyMap.get("자체생산"), 1, 3);
        // Add engine-specific water pump only for Nu and Theta
        if (engineType.equals("05") || engineType.equals("06")) {
            addProduct(products, "40100", engineType, engineName, "워터펌프 어셈블리", companyMap.get("Aisin"), 1, 3);
        }

        // Additional cooling components for Theta
        if (engineType.equals("06")) {
            addProduct(products, "40400", engineType, engineName, "보조 냉각 모듈", companyMap.get("Modine"), 1, 3);
            addProduct(products, "40210", "06", engineName, "서모스탯", companyMap.get("대원산업"), 1, 1);
            addProduct(products, "40300", "06", engineName, "냉각수 호스 세트", companyMap.get("동아튜브"), 1, 1);
        }

        // Add Gamma-specific cooling hose
        if (engineType.equals("04")) {
            addProduct(products, "40300", "04", engineName, "냉각수 호스 세트", companyMap.get("동아튜브"), 1, 1);
        }

        // 6. Lubrication Module
        addProduct(products, "30000", engineType, engineName, "윤활 모듈", companyMap.get("자체생산"), 1, 3);
        Company oilPumpVendor = engineType.equals("06") ? companyMap.get("한일고무") : companyMap.get("대원공업");
        addProduct(products, "30100", engineType, engineName, "오일펌프 모듈", oilPumpVendor, 1, 3);
        // Only add Theta-specific oil filter, common oil filter is created in createCommonParts
        if (engineType.equals("06")) {
            addProduct(products, "30310", "06", engineName, "오일 필터 (레이싱용)", companyMap.get("한국필터"), 1, 1);
        }
        addProduct(products, "30210", engineType, engineName, "오일팬", companyMap.get("신성씰테크"), 1, 1);

        // 7. Valvetrain Module
        addProduct(products, "20000", engineType, engineName, "밸브트레인 모듈", companyMap.get("자체생산"), 1, 3);
        addProduct(products, "20100", engineType, engineName, "캠샤프트 모듈", companyMap.get("BorgWarner"), 1, 3);
        addProduct(products, "20200", engineType, engineName, "밸브 모듈", companyMap.get("우진스프링"), 1, 3);

        // Only add Theta-specific timing module, common timing module is created in createCommonParts
        if (engineType.equals("06")) {
            addProduct(products, "20300", "06", engineName, "타이밍 모듈 (고성능)", companyMap.get("Schaeffler"), 1, 3); // 정밀가공
        }
    }

    private void addProduct(List<Product> products, String basePartNum, String engineType, String engineName,
            String productName, Company company, int eachCount, int leadTime) {
        Product product = Product.builder()
                .productNumber(basePartNum + "-" + engineType + "P00")
                .productName(productName)
                .eachCount(eachCount)
                .safeItemCount(0) // safe_item_count를 0으로 설정
                .leadTime(leadTime)
                .applicableEngine(engineName)
                .company(company)
                .build();
        products.add(product);
    }

    private Company getFuelSupplyVendor(String engineType, Map<String, Company> companyMap) {
        switch (engineType) {
            case "03":
            case "06":
                return companyMap.get("Bosch");
            case "04":
                return companyMap.get("Continental");
            case "05":
                return companyMap.get("Delphi");
            default:
                throw new IllegalArgumentException("Invalid engine type: " + engineType);
        }
    }

    private Company getIntakeVendor(String engineType, Map<String, Company> companyMap) {
        switch (engineType) {
            case "03":
            case "06":
                return companyMap.get("Bosch");
            case "04":
                return companyMap.get("Continental");
            case "05":
                return companyMap.get("Delphi");
            default:
                throw new IllegalArgumentException("Invalid engine type: " + engineType);
        }
    }

    private Company getExhaustVendor(String engineType, Map<String, Company> companyMap) {
        switch (engineType) {
            case "03":
            case "04":
            case "05":
                return companyMap.get("Denso");
            case "06":
                return companyMap.get("Bosch");
            default:
                throw new IllegalArgumentException("Invalid engine type: " + engineType);
        }
    }

    private Company getIgnitionVendor(String engineType, Map<String, Company> companyMap) {
        switch (engineType) {
            case "03":
            case "04":
            case "05":
                return companyMap.get("Denso");
            case "06":
                return companyMap.get("NGK");
            default:
                throw new IllegalArgumentException("Invalid engine type: " + engineType);
        }
    }

    private String getEngineTypeFromPartNum(String partNum) {
        return partNum.substring(6, 8);
    }

    private void createCommonParts(List<Product> products, Map<String, Company> companyMap) {
        // Common parts with engine type "01"
        addProduct(products, "40100", "01", "Kappa/Gamma/Nu 엔진", "워터펌프 어셈블리", companyMap.get("Aisin"), 1, 3); // 정밀가공
        addProduct(products, "40210", "01", "Kappa/Gamma/Nu 엔진", "서모스탯", companyMap.get("대원산업"), 1, 1); // 단순조립
        addProduct(products, "40300", "01", "Kappa/Gamma/Nu 엔진", "냉각수 호스 세트", companyMap.get("동아튜브"), 1, 1); // 단순조립
        addProduct(products, "30310", "01", "Kappa/Gamma/Nu 엔진", "오일 필터", companyMap.get("한국필터"), 1, 1); // 단순조립
        addProduct(products, "20300", "01", "Kappa/Gamma/Nu 엔진", "타이밍 모듈", companyMap.get("Continental"), 1, 3); // 정밀가공
    }

    private void initializeBins(List<Bay> bays) {
        List<Bin> bins = new ArrayList<>();
        final int BINS_PER_BAY = 10;

        for (Bay bay : bays) {
            for (int location = 1; location <= BINS_PER_BAY; location++) {
                Bin bin = Bin.builder()
                        .bay(bay)
                        .binLocation(location)
                        .stock(null)  // 초기에는 stock이 없음
                        .build();
                bins.add(bin);
            }
        }

        // 검증
        if (bins.size() != 32000) {  // 3200 * 10
            throw new IllegalStateException(
                    String.format("Expected 32000 bins but created %d", bins.size())
            );
        }

        binJpaRepository.saveAll(bins);
    }

}