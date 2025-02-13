package com.myme.mywarehome.domains.receipt.adapter.out.persistence;

import com.myme.mywarehome.domains.receipt.application.domain.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiptJpaRepository extends JpaRepository<Receipt, Long> {

}
