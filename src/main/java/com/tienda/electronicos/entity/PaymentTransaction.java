package com.tienda.electronicos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "payment_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reference", unique = true, length = 100)
    private String reference;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "status", length = 30)
    private String status; // CREATED, PENDING, APPROVED, REJECTED

    @Column(name = "buyer_email", length = 200)
    private String buyerEmail;

    @Column(name = "pse_transaction_id", length = 200)
    private String pseTransactionId;

    @Column(name = "pse_bank_code", length = 50)
    private String pseBankCode;

    @Column(name = "redirect_url", length = 1000)
    private String redirectUrl;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}

