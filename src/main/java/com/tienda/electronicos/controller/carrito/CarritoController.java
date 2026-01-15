package com.tienda.electronicos.controller.carrito;

import com.tienda.electronicos.dto.carrito.AddToCarritoRequest;
import com.tienda.electronicos.dto.carrito.CarritoResponse;
import com.tienda.electronicos.entity.Carrito;
import com.tienda.electronicos.mapper.CarritoMapper;
import com.tienda.electronicos.service.CarritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class CarritoController {

    private final CarritoService carritoService;

    // ===============================
    // AGREGAR PRODUCTO AL CARRITO
    // ===============================
    @PostMapping("/agregar")
    public ResponseEntity<CarritoResponse> agregarProducto(
            @RequestBody AddToCarritoRequest request
    ) {
        Carrito carrito = carritoService.agregarProducto(
                request.getProductoId(),
                request.getCantidad()
        );

        return ResponseEntity.ok(
                CarritoMapper.toResponse(carrito)
        );
    }

    // ===============================
    // VER CARRITO
    // ===============================
    @GetMapping
    public ResponseEntity<CarritoResponse> verCarrito() {

        Carrito carrito = carritoService.obtenerCarrito();

        return ResponseEntity.ok(
                CarritoMapper.toResponse(carrito)
        );
    }

    // ===============================
    // ELIMINAR PRODUCTO
    // ===============================
    @DeleteMapping("/eliminar/{productoId}")
    public ResponseEntity<CarritoResponse> eliminarProducto(
            @PathVariable("productoId") Long productoId
    ) {
        return ResponseEntity.ok(
                CarritoMapper.toResponse(
                        carritoService.eliminarProducto(productoId)
                )
        );
    }

    // ===============================
// ACTUALIZAR CANTIDAD DE PRODUCTO
// ===============================
    @PutMapping("/actualizar")
    public ResponseEntity<CarritoResponse> actualizarCantidad(
            @RequestBody AddToCarritoRequest request
    ) {
        Carrito carrito = carritoService.actualizarCantidad(
                request.getProductoId(),
                request.getCantidad()
        );

        return ResponseEntity.ok(
                CarritoMapper.toResponse(carrito)
        );
    }


}
