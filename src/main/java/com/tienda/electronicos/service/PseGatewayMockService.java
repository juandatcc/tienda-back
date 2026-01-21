package com.tienda.electronicos.service;

import com.tienda.electronicos.dto.payment.CreatePaymentRequest;
import com.tienda.electronicos.dto.payment.CreatePaymentResponse;
import com.tienda.electronicos.repository.PseGatewayService;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class PseGatewayMockService implements PseGatewayService {

    @Override
    public CreatePaymentResponse initiatePayment(CreatePaymentRequest request, String reference) {
        // Mock: crear una URL de redirección que simula la pasarela PSE
        String base = "/pse/mock/checkout"; // ruta interna del backend que simula checkout
        String params = "?reference=" + URLEncoder.encode(reference, StandardCharsets.UTF_8) +
                "&amount=" + URLEncoder.encode(request.getAmount().toString(), StandardCharsets.UTF_8) +
                "&returnUrl=" + URLEncoder.encode(request.getReturnUrl(), StandardCharsets.UTF_8);

        String redirectUrl = base + params;
        return new CreatePaymentResponse(reference, redirectUrl);
    }
}

