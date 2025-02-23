package com.myme.mywarehome.domains.company.adapter.out.persistence;

import com.myme.mywarehome.domains.company.application.domain.Company;
import com.myme.mywarehome.domains.product.application.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyJpaRepository extends JpaRepository<Company, Long> {
    @Query("""
    SELECT c FROM Company c 
    WHERE c.isVendor = true 
    AND (
        COALESCE(:code, '') = '' AND COALESCE(:name, '') = ''
        OR
        (COALESCE(:code, '') <> '' AND LOWER(c.companyCode) LIKE LOWER(CONCAT('%', :code, '%')))
        OR 
        (COALESCE(:name, '') <> '' AND LOWER(c.companyName) LIKE LOWER(CONCAT('%', :name, '%')))
    )
    ORDER BY c.createdAt DESC
""")
    Page<Company> findVendorsByConditions(
            @Param("code") String code,
            @Param("name") String name,
            Pageable pageable
    );

    @Query("SELECT c FROM Company c WHERE c.companyId = :companyId AND c.isVendor = true")
    Page<Company> findVendorByCompanyId(
            @Param("companyId") Long companyId,
            Pageable pageable
    );

    @Query("SELECT p FROM Product p JOIN FETCH p.company c " +
            "WHERE c.isVendor = false " +
            "AND (" +
            "   CASE WHEN (COALESCE(:productNumber, '') != '' OR COALESCE(:productName, '') != '') "
            +
            "   THEN (" +
            "       (COALESCE(:productNumber, '') != '' AND LOWER(p.productNumber) LIKE LOWER(CONCAT('%', :productNumber, '%'))) " +
            "       OR (COALESCE(:productName, '') != '' AND LOWER(p.productName) LIKE LOWER(CONCAT('%', :productName, '%'))) " +
            "   ) " +
            "   ELSE true END" +
            ") " +
            "AND (COALESCE(:applicableEngine, '') = '' OR LOWER(p.applicableEngine) LIKE LOWER(CONCAT('%', :applicableEngine, '%')))")
    Page<Product> findInhouseByConditions(
            @Param("productNumber") String productNumber,
            @Param("productName") String productName,
            @Param("applicableEngine") String applicableEngine,
            Pageable pageable
    );
}


