package com.tienda.electronicos.repository;

import com.tienda.electronicos.dto.payment.CreatePaymentRequest;
import com.tienda.electronicos.dto.payment.CreatePaymentResponse;

public interface PseGatewayService {
    CreatePaymentResponse initiatePayment(CreatePaymentRequest request, String reference);
}
