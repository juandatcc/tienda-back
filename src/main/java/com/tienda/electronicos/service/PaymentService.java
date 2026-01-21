package com.tienda.electronicos.service;

import com.tienda.electronicos.dto.payment.CreatePaymentRequest;
import com.tienda.electronicos.dto.payment.CreatePaymentResponse;
import com.tienda.electronicos.entity.PaymentTransaction;
import com.tienda.electronicos.repository.PaymentTransactionRepository;
import com.tienda.electronicos.repository.PseGatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentTransactionRepository transactionRepository;
    private final PseGatewayService pseGatewayService;

    public CreatePaymentResponse createPayment(CreatePaymentRequest request) {
        // generar referencia única
        String reference = "TX-" + UUID.randomUUID().toString();

        PaymentTransaction tx = PaymentTransaction.builder()
                .reference(reference)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status("CREATED")
                .buyerEmail(request.getBuyerEmail())
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        transactionRepository.save(tx);

        CreatePaymentResponse response = pseGatewayService.initiatePayment(request, reference);

        // actualizar con redirectUrl y estado PENDING
        tx.setRedirectUrl(response.getRedirectUrl());
        tx.setStatus("PENDING");
        tx.setUpdatedAt(OffsetDateTime.now());
        transactionRepository.save(tx);

        return response;
    }

    public PaymentTransaction findByReference(String reference) {
        return transactionRepository.findByReference(reference).orElse(null);
    }

    public void updateStatus(String reference, String status, String pseTransactionId, String bankCode) {
        transactionRepository.findByReference(reference).ifPresent(tx -> {
            tx.setStatus(status);
            tx.setPseTransactionId(pseTransactionId);
            tx.setPseBankCode(bankCode);
            tx.setUpdatedAt(OffsetDateTime.now());
            transactionRepository.save(tx);
        });
    }
}

