package com.myme.mywarehome.domains.mrp.adapter.out;

import com.myme.mywarehome.domains.mrp.application.port.out.GetAllProductPort;
import com.myme.mywarehome.domains.product.adapter.out.persistence.ProductJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllProductAdapter implements GetAllProductPort {
    private final ProductJpaRepository productJpaRepository;

    @Override
    public List<String> getAllProductNumbers() {
        return productJpaRepository.findAllProductNumbers();
    }
}
