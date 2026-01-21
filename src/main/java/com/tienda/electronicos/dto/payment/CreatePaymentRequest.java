package com.tienda.electronicos.dto.payment;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreatePaymentRequest {

    @NotNull
    private BigDecimal amount;

    @NotBlank
    private String currency;

    @NotBlank
    @Email
    private String buyerEmail;

    @NotBlank
    private String returnUrl; // URL donde redirigir al usuario después de pago (frontend)

}

