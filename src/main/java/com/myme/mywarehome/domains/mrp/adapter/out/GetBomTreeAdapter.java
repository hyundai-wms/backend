package com.myme.mywarehome.domains.mrp.adapter.out;

import com.myme.mywarehome.domains.mrp.adapter.out.persistence.BomTreeJpaRepository;
import com.myme.mywarehome.domains.mrp.application.domain.BomTree;
import com.myme.mywarehome.domains.mrp.application.port.out.GetBomTreePort;
import com.myme.mywarehome.domains.product.adapter.out.persistence.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetBomTreeAdapter implements GetBomTreePort {
    private final BomTreeJpaRepository bomTreeJpaRepository;
    private final ProductJpaRepository productJpaRepository;

    @Override
    public List<BomTree> findAllByApplicableEngine(String applicableEngine) {
        return productJpaRepository.findByApplicableEngine(applicableEngine)
                .stream()
                .filter(product -> product.getProductNumber().startsWith("10000-"))
                .findFirst()
                .map(product -> bomTreeJpaRepository.findAllByRootProduct(product.getProductNumber()))
                .orElse(Collections.emptyList());
    }
}
