package com.tienda.electronicos.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreatePaymentResponse {
    private String reference;
    private String redirectUrl; // URL to which frontend should redirect user to complete payment
}

