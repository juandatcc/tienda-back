package com.tienda.electronicos.controller.venta;

import com.tienda.electronicos.dto.venta.VentaResponse;
import com.tienda.electronicos.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','USER')")
public class VentaController {

    private final VentaService ventaService;

    // =====================================================
    // CONFIRMAR COMPRA
    // =====================================================
    @PostMapping("/confirmar")
    @ResponseStatus(HttpStatus.CREATED)
    public VentaResponse confirmarVenta() {
        return ventaService.realizarVenta();
    }
}
