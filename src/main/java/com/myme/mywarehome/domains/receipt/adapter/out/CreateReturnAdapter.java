package com.myme.mywarehome.domains.receipt.adapter.out;

import com.myme.mywarehome.domains.receipt.adapter.out.persistence.ReturnJpaRepository;
import com.myme.mywarehome.domains.receipt.application.domain.Return;
import com.myme.mywarehome.domains.receipt.application.port.out.CreateReturnPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateReturnAdapter implements CreateReturnPort {
    private final ReturnJpaRepository returnJpaRepository;

    @Override
    public Return create(Return returnEntity) {
        return returnJpaRepository.save(returnEntity);
    }
}
