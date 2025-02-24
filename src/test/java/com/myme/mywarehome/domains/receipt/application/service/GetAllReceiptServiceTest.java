package com.myme.mywarehome.domains.receipt.application.service;

import com.myme.mywarehome.domains.product.application.domain.Product;
import com.myme.mywarehome.domains.receipt.application.domain.Receipt;
import com.myme.mywarehome.domains.receipt.application.domain.ReceiptPlan;
import com.myme.mywarehome.domains.receipt.application.port.in.command.GetAllReceiptCommand;
import com.myme.mywarehome.domains.receipt.application.port.out.GetReceiptPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllReceiptServiceTest {

    @InjectMocks
    private GetAllReceiptService getAllReceiptService;

    @Mock
    private GetReceiptPort getReceiptPort;

    private Receipt receipt;
    private ReceiptPlan receiptPlan;
    private Product product;
    private Pageable pageable;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        pageable = PageRequest.of(0, 10);

        product = Product.builder()
                .productNumber("TEST001")
                .productName("테스트 상품")
                .build();

        receiptPlan = ReceiptPlan.builder()
                .receiptPlanId(1L)
                .receiptPlanCode("RP001")
                .receiptPlanDate(today)
                .receiptPlanItemCount(10)
                .product(product)
                .build();

        receipt = Receipt.builder()
                .receiptId(1L)
                .receiptCode("R001")
                .receiptDate(today)
                .receiptPlan(receiptPlan)
                .product(product)
                .build();
    }

    @Test
    @DisplayName("업체 정보로 입고 목록을 조회한다")
    void getAllReceipt_WithCompanyInfo_ReturnsFilteredReceiptPage() {
        // given
        GetAllReceiptCommand command = new GetAllReceiptCommand(
                "COMPANY001", "테스트 업체",
                null, null, null,
                null, null, null,
                null, null
        );

        List<Receipt> receipts = List.of(receipt);
        Page<Receipt> receiptPage = new PageImpl<>(receipts, pageable, receipts.size());

        when(getReceiptPort.findAllReceipts(command, pageable))
                .thenReturn(receiptPage);

        // when
        Page<Receipt> result = getAllReceiptService.getAllReceipt(command, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("입고 계획 정보로 입고 목록을 조회한다")
    void getAllReceipt_WithReceiptPlanInfo_ReturnsFilteredReceiptPage() {
        // given
        GetAllReceiptCommand command = new GetAllReceiptCommand(
                null, null,
                "RP001", today.minusDays(1), today.plusDays(1),
                null, null, null,
                null, null
        );

        List<Receipt> receipts = List.of(receipt);
        Page<Receipt> receiptPage = new PageImpl<>(receipts, pageable, receipts.size());

        when(getReceiptPort.findAllReceipts(command, pageable))
                .thenReturn(receiptPage);

        // when
        Page<Receipt> result = getAllReceiptService.getAllReceipt(command, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        Receipt foundReceipt = result.getContent().get(0);
        assertThat(foundReceipt.getReceiptPlan().getReceiptPlanCode()).isEqualTo(command.receiptPlanCode());
        assertThat(foundReceipt.getReceiptPlan().getReceiptPlanDate())
                .isBetween(command.receiptPlanStartDate(), command.receiptPlanEndDate());
    }

    @Test
    @DisplayName("상품 정보로 입고 목록을 조회한다")
    void getAllReceipt_WithProductInfo_ReturnsFilteredReceiptPage() {
        // given
        GetAllReceiptCommand command = new GetAllReceiptCommand(
                null, null, null, null, null,
                product.getProductNumber(), product.getProductName(),
                null, null, null
        );

        List<Receipt> receipts = List.of(receipt);
        Page<Receipt> receiptPage = new PageImpl<>(receipts, pageable, receipts.size());

        when(getReceiptPort.findAllReceipts(command, pageable))
                .thenReturn(receiptPage);

        // when
        Page<Receipt> result = getAllReceiptService.getAllReceipt(command, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        Receipt foundReceipt = result.getContent().get(0);
        assertThat(foundReceipt.getReceiptPlan().getProduct().getProductNumber()).isEqualTo(command.productNumber());
        assertThat(foundReceipt.getReceiptPlan().getProduct().getProductName()).isEqualTo(command.productName());
    }

    @Test
    @DisplayName("입고 정보로 입고 목록을 조회한다")
    void getAllReceipt_WithReceiptInfo_ReturnsFilteredReceiptPage() {
        // given
        GetAllReceiptCommand command = new GetAllReceiptCommand(
                null, null, null, null, null,
                null, null,
                "R001", today.minusDays(1), today.plusDays(1)
        );

        List<Receipt> receipts = List.of(receipt);
        Page<Receipt> receiptPage = new PageImpl<>(receipts, pageable, receipts.size());

        when(getReceiptPort.findAllReceipts(command, pageable))
                .thenReturn(receiptPage);

        // when
        Page<Receipt> result = getAllReceiptService.getAllReceipt(command, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        Receipt foundReceipt = result.getContent().get(0);
        assertThat(foundReceipt.getReceiptCode()).isEqualTo(command.receiptCode());
        assertThat(foundReceipt.getReceiptDate())
                .isBetween(command.receiptStartDate(), command.receiptEndDate());
    }

    @Test
    @DisplayName("검색 조건 없이 입고 목록을 조회한다")
    void getAllReceipt_WithNoConditions_ReturnsReceiptPage() {
        // given
        GetAllReceiptCommand command = new GetAllReceiptCommand(
                null, null, null, null, null,
                null, null, null, null, null
        );

        List<Receipt> receipts = List.of(receipt);
        Page<Receipt> receiptPage = new PageImpl<>(receipts, pageable, receipts.size());

        when(getReceiptPort.findAllReceipts(any(GetAllReceiptCommand.class), any(Pageable.class)))
                .thenReturn(receiptPage);

        // when
        Page<Receipt> result = getAllReceiptService.getAllReceipt(command, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getNumber()).isZero();
        assertThat(result.getSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("조건에 맞는 입고가 없을 때 빈 페이지를 반환한다")
    void getAllReceipt_WithNoMatchingResults_ReturnsEmptyPage() {
        // given
        GetAllReceiptCommand command = new GetAllReceiptCommand(
                "NON_EXISTENT", null, null, null, null,
                null, null, null, null, null
        );

        Page<Receipt> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(getReceiptPort.findAllReceipts(command, pageable))
                .thenReturn(emptyPage);

        // when
        Page<Receipt> result = getAllReceiptService.getAllReceipt(command, pageable);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }
}