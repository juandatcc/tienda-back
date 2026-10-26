package com.tienda.electronicos.service;

import com.tienda.electronicos.dto.payment.CreatePaymentRequest;
import com.tienda.electronicos.dto.payment.CreatePaymentResponse;

public interface PseGatewayService {
    CreatePaymentResponse initiatePayment(CreatePaymentRequest request, String reference);
}
