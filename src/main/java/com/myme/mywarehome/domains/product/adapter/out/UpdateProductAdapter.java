package com.myme.mywarehome.domains.product.adapter.out;

import com.myme.mywarehome.domains.product.adapter.out.persistence.ProductJpaRepository;
import com.myme.mywarehome.domains.product.application.port.in.command.UpdateSafeItemCountCommand;
import com.myme.mywarehome.domains.product.application.port.out.UpdateProductPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UpdateProductAdapter implements UpdateProductPort {
    private final ProductJpaRepository productJpaRepository;

    @Override
    @Transactional
    public void updateAllSafeItemCount(List<UpdateSafeItemCountCommand> commands) {
        for (UpdateSafeItemCountCommand command : commands) {
            productJpaRepository.updateSafeItemCount(
                    command.productNumber(),
                    command.safeItemCount()
            );
        }
    }
}
