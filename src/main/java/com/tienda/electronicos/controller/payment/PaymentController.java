package com.tienda.electronicos.controller.payment;

import com.tienda.electronicos.dto.payment.CreatePaymentRequest;
import com.tienda.electronicos.dto.payment.CreatePaymentResponse;
import com.tienda.electronicos.entity.PaymentTransaction;
import com.tienda.electronicos.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pse")
    public ResponseEntity<CreatePaymentResponse> createPsePayment(@RequestBody @Valid CreatePaymentRequest request) {
        CreatePaymentResponse response = paymentService.createPayment(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{reference}")
    public ResponseEntity<PaymentTransaction> getStatus(@PathVariable String reference) {
        PaymentTransaction tx = paymentService.findByReference(reference);
        if (tx == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(tx);
    }

    // Endpoint público que simula el flujo de checkout de la pasarela PSE (mock)
    @GetMapping("/pse/mock/checkout")
    public ResponseEntity<String> mockCheckout(
            @RequestParam String reference,
            @RequestParam String amount,
            @RequestParam String returnUrl
    ) {
        // Simular que el banco procesa y redirige al returnUrl con status
        // Para simplificar, generamos un pseTransactionId y consideramos pago aprobado
        String pseTransactionId = "PSE-" + java.util.UUID.randomUUID();
        // Normalmente la pasarela haría POST a tu endpoint de callback
        // Aquí simulamos redirección con parámetros
        String redirect = returnUrl + "?reference=" + reference + "&status=APPROVED&transactionId=" + pseTransactionId + "&bankCode=001";

        // Actualizar estado en DB
        paymentService.updateStatus(reference, "APPROVED", pseTransactionId, "001");

        // Redirigir (en una implementación real devolveríamos 302); aquí devolvemos la URL de redirección
        return ResponseEntity.ok(redirect);
    }
}

