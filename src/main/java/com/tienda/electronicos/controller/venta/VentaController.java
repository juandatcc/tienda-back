package com.tienda.electronicos.controller.venta;

import com.tienda.electronicos.entity.Venta;
import com.tienda.electronicos.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    // =====================================================
    // CONFIRMAR COMPRA
    // =====================================================
    @PostMapping("/confirmar")
    @ResponseStatus(HttpStatus.CREATED)
    public Venta confirmarVenta() {
        return ventaService.realizarVenta();
    }
}
